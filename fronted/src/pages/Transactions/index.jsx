import { useEffect, useRef, useState } from "react";
import { DividerCard } from "../../components/Divider/DividerCard";
import { Navbar } from "../../components/Navbar";
import { TextField } from "../../components/TextField";
import { Page } from "../../constants/Page";
import { TextFieldTypes } from "../../constants/TextFieldType";
import { getTransactions, getTransactionsByFilter } from "../_services/transactions";
import { TransactionType } from "../../constants/TransactionType";
import { StatusError } from "../../errors/StatusError";
import { getTraduction } from "../../utils/getTraduction";
import { Traduction } from "../../constants/Traduction";
import { Link } from "react-router-dom";
import { Fab } from "../../components/Buttons/FAB";
import { Spin } from "../../components/Loader/Spin";
import { SEO } from "../../utils/SEO";
import { InputTypes } from "../../constants/InputType";

/** @type {string} */
const language = localStorage.getItem("language") ?? navigator.language;

/**
 * According to the desire language, return an array with months traduction
 * @param {string} language The desire language
 * @returns
 */
const months = (language) => (language == "es") ? ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]
   : ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

/**
 * With the date provided, return a date with the language and style format 
 * @param {string} date The value to format
 * @returns 
 */
const getFormattedDate = (date) => {
   return new Intl.DateTimeFormat(language, { dateStyle: "long" }).format(new Date(date));
};

/**
 * @typedef {object} TransactionList
 * @param {`${string}-${string}-${string}-${string}-${string}`} id
 * @param {string} date
 * @param {{ 
 *    idTransaction: number, 
 *    idTransferAccount: String, 
 *    receiverName: String,
 *    transactionAmount: number,
 *    transactionType: string,
 *    transactionTimestamp: string,
 *    isAutomated: boolean
 * }} transactions
 */

/**
 * Return an array with the transactions's date and a new array with all transactions according to the date
 * @param {Array<TransactionList>} pastTransactions The value with the transactions already formatted
 * @param {Array<TransactionList>} content The value with new transactions to be formatted
 * @returns {Array<TransactionList>}
 */
const getModifiedContent = (pastTransactions, content) => {
   const isDupe = pastTransactions?.some((group) => {
      return group.transactions.some(/** @param {Object} transaction */(transaction) => content.some((value) => {
         return value?.idTransaction === transaction?.idTransaction;
      }));
   });

   if (!isDupe) {
      for (const cont of content) {
         const newDate = getFormattedDate(cont.transactionTimestamp);
         if (pastTransactions.some((group => group.date === newDate))) {
            pastTransactions[pastTransactions.length - 1].transactions.push(cont);
         } else {
            pastTransactions.push({
               id: crypto.randomUUID(),
               date: newDate,
               transactions: []
            });
            pastTransactions[pastTransactions.length - 1].transactions.push(cont);
         }
      }
   }
   
   return pastTransactions;
};

/**
 * Page containing the logic to get transactions according to the filters, or by default
 * @returns 
 */
export const Transactions = () => {
   /** @type {[{ default: Array<TransactionList>, search: Array<TransactionList> }, import("react").Dispatch<import("react").SetStateAction<{ default: Array<TransactionList>, search: Array<TransactionList> }>>]} */
   const [allTransactions, setAllTransactions] = useState({
      default: [],
      search: []
   });
   /** @type {[{ type: string, name: string, date: string[] }, import("react").Dispatch<import("react").SetStateAction<{ type: string, name: string, date: string[] }>>]} */
   const [texts, setTexts] = useState({
      type: "",
      name: "",
      date: []
   });
   /** @type {[[string, string, string], import("react").Dispatch<import("react").SetStateAction<[string, string, string]>>]} */
   const [modalValues, setModalValues] = useState(["", "", ""]);
   /** @type {[{ default: number, search: number}, import("react").Dispatch<import("react").SetStateAction<{ default: number, search: number}>>]} */
   const [page, setPage] = useState({
      default: 0,
      search: 0,
   });
   /** @type {[boolean, import("react").Dispatch<import("react").SetStateAction<boolean>>]} */
   const [notFound, setNotFound] = useState(false); 
   /** @type {[boolean, import("react").Dispatch<import("react").SetStateAction<boolean>>]} */
   const [loading, setLoading] = useState(true);
   const t = getTraduction(Traduction.TRANSACTIONS_PAGE);

   /** @type {import("react").MutableRefObject<HTMLInputElement>} */
   const typeReference = useRef();
   /** @type {import("react").MutableRefObject<HTMLInputElement>} */
   const textReference = useRef();
   /** @type {import("react").MutableRefObject<HTMLInputElement>} */
   const dateReference = useRef();
   /** @type {import("react").MutableRefObject<HTMLFormElement>} */
   const formToDialogReference = useRef();
   /** @type {import("react").MutableRefObject<HTMLDivElement>} */
   const transactionsContainer = useRef();

   const whichContainer = (globalThis.matchMedia?.("(min-width: 768px)").matches) ? transactionsContainer.current : globalThis;

   /** @type {{ idAccount: number }} */
   const { idAccount } = JSON.parse(localStorage.getItem("account"));

   /** When inputs change, and assign different values to fetch or to improve the user feedback */
   const handleChange = () =>{
      const text = textReference.current?.value;
      const type = typeReference.current?.value;

      setAllTransactions({ ...allTransactions, search: [] });
      setPage({ ...page, search: 0 });  

      setNotFound(false);
      setLoading(true);

      setTexts({
         type: type,
         name: text,
         date: modalValues
      });

      whichContainer?.removeEventListener?.("scrollend", () => setPage({
         ...page,
         search: page.search + 1
      }));
   };

   /** When modal inputs change, set the respective values */
   const handleModalValues = () => {
      const menuInputs = formToDialogReference.current?.getElementsByTagName("input");
      setModalValues([menuInputs[3].value, menuInputs[4].value, menuInputs[5].value]);
   };

   /**
    * According to the year and month provided, return the last day of the month
    * @param {number} year The value to the desire year
    * @param {string} month The value to the desire month
    * @returns The last day of the desire month
    */
   const getMaxDaysInAMonth = (year = 2023, month) => {
      return new Date(year, months(language).indexOf(month) + 1, 0).getDate();
   };

   /**
    * Check if day is in the respective days of the month
    * @param {number} year The value to the desire year
    * @param {string} month The value to the desire month
    * @param {number} day The value to the desire day
    * @returns 
    */
   const inDaysOfAMonth = (year, month, day) => {
      const MAX_DAY = getMaxDaysInAMonth(year, month);
      return day >= 1 && day <= MAX_DAY;
   };

   useEffect(() => {
      setLoading(true);

      setTimeout(async () => {         
         try {
            /** @type {Array<TransactionList>} */
            let content = [{}];
            /** @type {boolean} */
            let last = false;
            /** @type {string} */
            let type = "default";
            /** @type {number} */
            let pageContent = page[type] + 1;
   
            if (dateReference?.current.value || texts.name || texts.type) {
               whichContainer?.removeEventListener?.("scrollend", () => setPage({
                  ...page,
                  default: page.default + 1
               }));
   
               pageContent = page.search + 1;

               let transactionType = "";
               let formattedMonth;
      
               if (texts.type) {
                  for (const type of Object.entries(TransactionType)) {
                     if (type[1].description.includes(texts.type)) {
                        transactionType = type[0];
                     }
                  }
               }

               const wantedMonth = months(language);

               for (let i = 0; i < wantedMonth.length - 1; i++) {
                  if(texts.date[1] == wantedMonth[i]) {
                     formattedMonth = months("en")[i].toUpperCase();
                  }
               }

               /** @type {{ content: Array<TransactionList>, last: boolean }} */
               const { content: filterContent, last: filterLast } = await getTransactionsByFilter({
                  id: idAccount,
                  type: transactionType,
                  name: texts.name,
                  date: dateReference?.current?.value ? {
                     year: Number(modalValues[0]),
                     month: modalValues[1] ? formattedMonth : null,
                     day: (formattedMonth && inDaysOfAMonth(Number(modalValues[0]), modalValues[1], Number(modalValues[2]))) ? modalValues[2] : null
                  } : {},
                  page: page.search,
               });

               content = filterContent;
               last = filterLast;
   
               type = "search";
            } else {
               /** @type {{ content: Array<TransactionList>, last: boolean }} */
               const { content: defaultContent, last: defaultLast } = await getTransactions(idAccount, page?.default);
   
               content = defaultContent;
               last = defaultLast;
            }
            
            const pastTransactions = getModifiedContent(allTransactions[type], content);
   
            setAllTransactions({
               ...allTransactions,
               [type]: pastTransactions
            });  

            setNotFound(false);
            setLoading(false);
            if (last) {
               setLoading(false);
               whichContainer?.removeEventListener?.("scrollend", () => setPage((prevPage) => {
                  return {
                     ...prevPage,
                     [type]: pageContent
                  };
               }));
            } else {
               whichContainer?.addEventListener?.("scrollend", () => setPage({
                  ...page,
                  [type]: pageContent
               }));
   
               if (texts.name || texts.date) {
                  whichContainer?.removeEventListener?.("scrollend", () => setPage({
                     ...page,
                     default: page.default + 1
                  }));
               }
            }
         } catch (error) {
            if (error instanceof StatusError) {
               setNotFound(true);
               setLoading(false);
               setAllTransactions({ ...allTransactions, search: [] });  
            }
         }
      }, 500);

      return whichContainer?.removeEventListener?.("scrollend", () => setPage({
         ...page,
         search: page.search + 1
      }));
   }, [page, texts.type, texts.name, texts.date]);

   /** @type {TransactionList} */
   let transactionsGroup = allTransactions.default;

   if (texts.date[0] || texts.name || texts.type) transactionsGroup = allTransactions.search;

   return (
      <section className="h-full md:h-screen md:flex md:flex-row-reverse md:overflow-hidden">
         <SEO title={t.seo.title} description={t.seo.description} />
         <div className="w-full min-h-[calc(100vh-5rem)] bg-white border-outline-variant md:border md:rounded-2xl md:mx-6 md:my-4 
            md:pb-2 dark:bg-black dark:border-outline-variant-dark">
            <form 
               ref={formToDialogReference}
               className="flex flex-col gap-3 pt-3 px-4 md:px-6 mb-3 md:pt-4"
            >
               <TextField
                  label={t.labels[0]}
                  type={TextFieldTypes.MENU}
                  valueRef={typeReference}
                  functionToUpdate={handleChange}
                  menuParameters={Object.values(TransactionType).map((type) => type.description)}
                  menuClasses="w-[calc(100%-2rem)] md:w-[calc(100%-11rem)]"
               />
               <TextField 
                  label={t.labels[1]}
                  type={TextFieldTypes.SEARCH}
                  valueRef={textReference}
                  functionToUpdate={handleChange}
               />
               <TextField 
                  label={t.labels[2]}
                  type={TextFieldTypes.MODAL}
                  valueRef={dateReference}
                  functionToUpdate={handleChange}
                  modalParameters={[{
                     label: t.modal.labels[0],
                     textFieldType: TextFieldTypes.MENU,
                     initialInputType: InputTypes.NUMBER,
                     value: 2023,
                     menuParameters: [2023, 2022, 2021, 2020, 2019],
                     functionToUpdate: handleModalValues
                  }, {
                     label: t.modal.labels[1],
                     textFieldType: TextFieldTypes.MENU,
                     initialInputType: InputTypes.TEXT,
                     support: (!modalValues[0]) ? t.modal.supportText[0] : "",
                     error: (modalValues[1] && !modalValues[0]),
                     isDisable: !modalValues[0],
                     menuParameters: months(language),
                     functionToUpdate: handleModalValues
                  }, {
                     label: t.modal.labels[2],
                     textFieldType: TextFieldTypes.DEFAULT,
                     initialInputType: InputTypes.NUMBER,
                     support: (!modalValues[1]) ? t.modal.supportText[1] : t.modal.supportText[2] 
                        + getMaxDaysInAMonth(Number(modalValues[0]), modalValues[1]),
                     error: (modalValues[2]) ? !inDaysOfAMonth(Number(modalValues[0]), modalValues[1], Number(modalValues[2])) : false,
                     isDisable: (!modalValues[0] || !modalValues[1]),
                     functionToUpdate: handleModalValues
                  }]}
               />
            </form>
            <h2 className="text-lg font-medium font-sans ml-4 underline md:ml-6 text-onSurface dark:text-onSurface-dark">{t.title}</h2>
            {notFound && <p className="pl-6 text-onSurface dark:text-onSurface-dark">{t.notFound}</p>}
            <div ref={transactionsContainer}  className="md:h-[calc(100%-16rem)] md:overflow-y-scroll">
               {transactionsGroup?.map(/** @param {Object} group */(group) => (
                  <>
                     <div key={group?.id} className="mt-3 pl-4 pb-1 border-b border-primary dark:border-primary-dark">
                        <h3 className="text-sm font-medium font-sans text-onSurface dark:text-onSurface-dark">{group?.date}</h3>
                     </div>
                     {group?.transactions?.map(/** @param {Object} transaction */(transaction) => {
                        const formattedType = TransactionType[transaction?.transactionType]?.description;
                        return (
                           <>
                              <DividerCard 
                                 key={transaction?.idTransaction}
                                 transferAccount={transaction?.idTransferAccount}
                                 name={transaction?.receiverName}
                                 amount={transaction?.transactionAmount?.toFixed(2)}
                                 type={formattedType}
                                 time={new Intl.DateTimeFormat(language, { dateStyle: "full", timeStyle: "medium" })
                                    .format(new Date(transaction?.transactionTimestamp))}
                                 automated={transaction?.isAutomated}
                              />
                           </>
                        );
                     })}
                  </>
               ))}
               {loading && <Spin />}
            </div>
         </div>
         <div className="w-full h-20 md:w-fit md:h-fit">
            <Navbar page={Page.TRANSACTIONS} >
               <Link className="group/fab outline-none" to="/transaction">
                  <Fab label={(globalThis.matchMedia?.("(min-width: 768px)").matches) ? t.fab.large : t.fab.small} />
               </Link>
            </Navbar>
         </div>
      </section>
   );
};