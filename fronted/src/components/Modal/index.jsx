import { useEffect, useId, useRef, useState } from "react";
import "./Modal.css";
import { getTraduction } from "../../utils/getTraduction";
import { Traduction } from "../../constants/Traduction";

const valueContainer = "bg-surface-container-highest outline-none border-b border-onSurface-variant focus:border-primary caret-primary text-onSurface text-base font-sans font-normal";
const h2Element = (title) => <h2 className="text-2xl font-normal font-sans text-onSurface mb-4 dark:text-onSurface-dark">{title}</h2>;
const buttonStyles = "text-sm font-medium font-sans text-primary dark:text-primary-dark";

const ListModal = ({ 
   title, 
   listUtils, 
   children 
}) => {
   return (
      <div className="w-full flex flex-col justify-center items-center p-6">
         {h2Element(title)}
         <div className="w-full flex flex-row justify-between">
            {Object.entries(listUtils?.parameters)?.map((array) => (
               <div key={array[0]} className="flex flex-col justify-center items-center">
                  <label htmlFor={array[0]} className="text-lg font-medium font-sans text-onSurface mb-2">{array[0]}</label>
                  <select 
                     id={array[0]} 
                     name={array[0]}
                     className={`w-fit pl-1 ${valueContainer}`} type="text"
                  >
                     {array[1]?.map((element) => (
                        <option key={element}>{element}</option>
                     ))}
                  </select> 
               </div>
            ))}
         </div>
         <div className="w-full inline-flex justify-end items-end gap-4 mt-6">
            {children}
         </div>
      </div>
   );
};

const FormModal = ({ title, errorMessage, formUtils, children }) => {
   const [parameters, setParameters] = useState(formUtils?.errorParameters);
   const inputId = useId();
   const errorId = useId();

   const handleSubmit = (event) => {
      event.preventDefault();
      if (event.target.elements[0].value && event.target.elements[1].value) formUtils?.handle(event.target.elements[0].value, event.target.elements[1].value); 
      else {
         setParameters({
            first: !event.target.elements[0].value ? errorMessage : "",
            second: !event.target.elements[1].value ? errorMessage : ""
         });
      }
   };

   useEffect(() => {
      setParameters(formUtils?.errorParameters);
   }, [formUtils?.errorParameters]);

   return (
      <form 
         method="dialog"
         className="w-full flex flex-col justify-center items-center p-6"
         onSubmit={handleSubmit}
      >
         {h2Element(title)}
         {!formUtils?.successMessage && <>
            <div className="w-full flex flex-col justify-between">
                  <label htmlFor={inputId + "-first"}>{formUtils?.inputs[0]}</label>
                  <input 
                     id={inputId + "-first"}
                     type={formUtils?.inputs[0]?.match("email") ? "email" : "text"}
                     className={`${valueContainer}`}
                     aria-errormessage={(parameters?.first) ? errorId + "-first" : ""}
                     aria-invalid={Boolean(parameters?.first)}
                  />
                  {parameters?.first && <span id={errorId + "-first"} className={"ml-4 mt-1 text-sm text-error"}>{parameters?.first}</span>}
                  <label htmlFor={inputId + "-second"}>{formUtils?.inputs[1]}</label>
                  <input 
                     id={inputId + "-second"}
                     type="text" 
                     className={`${valueContainer}`}
                     aria-errormessage={(parameters?.second) ? errorId + "-second" : ""}
                     aria-invalid={Boolean(parameters?.second)}
                  />
                  {parameters?.second && <span id={errorId + "-second"} className={"ml-4 mt-1 text-sm text-error"}>{parameters?.second}</span>}
            </div>
            <div className="w-full inline-flex justify-end items-end gap-4 mt-6">
               {children}
            </div>
         </>}
         {formUtils?.successMessage && <>
            <p>{formUtils?.successMessage}</p>
            <div className="w-full inline-flex justify-end items-end gap-4 mt-6">
                  {children}
            </div>
         </>}
      </form>
   );
};

export const Modal = ({
   dialogRef,
   title,
   listUtils,
   formUtils,
   messageUtils
}) => {
   const storyRef = useRef();
   const { formatText, errorMessage, cancel, accept } = getTraduction(Traduction.MODAL);
   const { modalParameters } = getTraduction(Traduction.AUTOMATION_PAGE);

   const handleValues = () => {
      const values = [];
      let formattedValue;
      const reference = dialogRef ?? storyRef;
      const selects = reference?.current.getElementsByTagName("select");

      if (selects) {
         for (const target of selects) {
            if (target.value) {
               values.push(target.value);
            }
         }
   
         if (listUtils?.parameters[modalParameters[0]]) {
            formattedValue = Number(values[0]) * 168;
            formattedValue = formattedValue + Number(values[1]) * 24;
            formattedValue = formattedValue + Number(values[2]);
            
            formattedValue = `${formatText[0]} ${formattedValue} ${formatText[1]}`;
         }
   
         listUtils?.setValue(formattedValue ?? values.join().replace(",", " "));
      }
   };

   const showModal = () => {
      storyRef?.current?.showModal?.();
   };

   const closeModal = () => {
      dialogRef?.current?.close?.();
      storyRef?.current?.close?.();
   };

   const clearModal = () => {
      formUtils?.setError("");
      formUtils?.setSuccessMessage("");
      const inputs = dialogRef?.current?.getElementsByTagName("input");

      for (const input of inputs) {
         input.value = "";
      }
   };
   
   return (
      <>
         {dialogRef === undefined && <button onClick={showModal}>Modal</button>}
         <dialog 
            ref={dialogRef ?? storyRef} 
            className="w-[calc(100%-1rem)] min-w-[11rem] max-w-[24rem] shadow-md rounded-[1.75rem] bg-surface-container-high dark:bg-surface-container-high-dark"
            onClose={(formUtils?.inputs[0].match("email") && formUtils?.successMessage) ? formUtils?.closeSession : clearModal}
         >
            {listUtils && <ListModal 
               title={title}
               listUtils={listUtils}
            >
               <button 
                  type="button"
                  onClick={() => { 
                     closeModal();
                     listUtils.setIsClicked?.();
                  }}
                  className={buttonStyles}
               >{cancel}</button>
               <button 
                  type="button"
                  onClick={() => {
                     handleValues();
                     closeModal();
                     listUtils?.setIsChange(!listUtils.isChange);
                     listUtils?.setIsClicked(false);
                  }}
                  className={buttonStyles}
               >{accept}</button>
            </ListModal>}
            {formUtils && <FormModal 
               title={title}
               errorMessage={errorMessage}
               formUtils={formUtils}
            >
               {!formUtils?.successMessage && <>
                  <button 
                     type="button"
                     onClick={() => { 
                        closeModal();
                        listUtils?.setIsClicked();
                     }}
                     className={buttonStyles}
                  >{cancel}</button>
                  <button 
                     type="submit"
                     className={buttonStyles}
                  >{accept}</button>
               </>}
               {formUtils?.successMessage && <>
                  <button 
                     type="button"
                     onClick={() => { 
                        closeModal();
                     }}
                     className={buttonStyles}
                  >{accept}</button>
               </>}
            </FormModal>}
            {messageUtils && <div className="w-full flex flex-col justify-center items-center p-6">
               {h2Element(title)}
               <p className="text-sm font-sans font-normal text-onSurface-variant dark:text-onSurface-variant-dark">{messageUtils?.message}</p>
               <div className="w-full inline-flex justify-center items-center gap-4 mt-6">
                  <button 
                     type="button"
                     onClick={() => { 
                        closeModal();
                     }}
                     className={buttonStyles}
                  >{cancel}</button>
                  <button 
                     type="button"
                     onClick={() => { 
                        messageUtils?.changeLanguage?.();
                        messageUtils?.closeSession?.();
                        closeModal();
                     }}
                     className={buttonStyles}
                  >{accept}</button>
               </div>
            </div>}
         </dialog>
      </>
   );
};