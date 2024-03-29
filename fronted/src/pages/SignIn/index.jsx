import { Link, useLocation, useNavigate } from "react-router-dom";
import { Filled } from "../../components/Buttons/Filled";
import { TextField } from "../../components/TextField";
import { InputTypes } from "../../constants/InputType";
import { TextFieldTypes } from "../../constants/TextFieldType";
import { getAccountData } from "../_services/account";
import { login as logUser, resetPassword } from "../_services/auth";
import { useRef, useState } from "react";
import { getTraduction } from "../../utils/getTraduction";
import { Traduction } from "../../constants/Traduction";
import { Bar } from "../../components/Loader/Bar";
import { SEO } from "../../utils/SEO";
import { useAuth } from "../../hooks/useAuth";
import { Outline } from "../../components/Buttons/Outline";
import { Modal } from "../../components/Modal";
import { validInputElement } from "../../utils/validInputElement";

/**
 * Page containing the logic to sign in
 * @returns 
 */
export const SignIn = () => {
   /** @type {[string, import("react").Dispatch<import("react").SetStateAction<string>>]} */
   const [error, setError] = useState("");
   /** @type {[boolean, import("react").Dispatch<import("react").SetStateAction<boolean>>]} */
   const [isLoading, setIsLoading] = useState(false);
   /** @type {[boolean, import("react").Dispatch<import("react").SetStateAction<boolean>>]} */
   const [isReset, setIsReset] = useState(false);
   const { login } = useAuth();
   const navigate = useNavigate();
   const { state } = useLocation();
   /** @type {import("react").MutableRefObject<HTMLDialogElement>} */
   const dialogRef = useRef();
   const t = getTraduction(Traduction.SIGN_IN_PAGE);

   /** 
    * @param {import("react").FormEvent<HTMLFormElement>} [event] 
    * @param {{ email: String, password: String }} [demoAccount]
    * */
   const handleSubmit = (event, demoAccount) => {
      let email;
      let password;
      if (event !== null) {
         event.preventDefault();
   
         const { elements } = event.currentTarget;
         const inputArray = (!isReset) ? validInputElement([elements[0], elements[1]]) : validInputElement([elements[0]]);
         email = inputArray?.[0].value;
         password =  inputArray?.[1]?.value;
      } else {
         email = demoAccount.email;
         password =  demoAccount.password;
      }
      
      if (!isReset) {
         setIsLoading(true);

         setTimeout(() => {
            logUser(email, password)
               .then((token) => {
                  localStorage.setItem("token", "Bearer " + token);
                  getAccountData(email)
                     .then(() => {
                        login();
                        navigate(state?.location?.pathname ?? "/");
                        globalThis.location.reload();
                     });
               }).catch((e) => {
                  const message = e.message;
                  setIsLoading(false);
                  setError(message);
               });
         }, 1000);
      } else {
         resetPassword(email);
         dialogRef.current?.showModal?.();
      }
   };

   return (
      <section className="flex justify-center items-center h-screen">
         <div className="flex flex-col justify-center items-center gap-4 w-full md:max-w-[75ch] h-full px-4 mx-auto border border-outline-variant
         bg-white md:rounded-2xl md:px-6 md:py-8 md:h-fit dark:border-outline-variant-dark dark:bg-black">
            <SEO title={t.seo.title} description={t.seo.description} />
            <h1 className="text-4xl font-bold font-sans text-center text-onSurface dark:text-onSurface-dark">{
            (!isReset) ? t.title : t.resetPassword.title }</h1>
            <form
               className="flex flex-col items-center gap-3 w-full"
               onSubmit={handleSubmit}   
            >
               <TextField
                  label={t.labels[0]}
                  type={TextFieldTypes.DEFAULT}
                  initialInputType={InputTypes.EMAIL}
                  supportiveText={error}
                  isError={Boolean(error)}
                  isDisable={isLoading}
                  autoComplete="email"
               />
               {(!isReset) &&
                  <TextField
                     label={t.labels[1]}
                     type={TextFieldTypes.PASSWORD}
                     supportiveText={error}
                     isError={Boolean(error)}
                     isDisable={isLoading}
                     autoComplete="current-password"
                  />
               }
               {isLoading && <Bar />}
               <Filled label={(!isReset) ? t.accept : t.resetPassword.accept} isDisable={isLoading} />
            </form>

            {(!isReset) &&
               <div className="flex flex-col gap-2 w-full items-center">
                  <Outline 
                     label={t.useDemo}  
                     isDisable={isLoading}
                     handleClick={() => dialogRef.current?.showModal?.()}
                  />
                  
                  <button 
                     className="text-sm font-normal font-sans text-primary dark:text-primary-dark"
                     onClick={() => {
                        setIsReset(true);
                        setError("");
                     }}
                  >
                     {t.links[0]}
                  </button>
                  <div className="text-sm font-normal font-sans">
                     <span className="text-onSurface-variant dark:text-onSurface-variant-dark">{t.links[1][0]} </span>
                     <Link to="/sign-up" className="text-primary dark:text-primary-dark hover:underline">
                        {t.links[1][1]}
                     </Link>
                  </div>
               </div>
            }

            {(isReset) &&
               <Outline 
                  label={t.resetPassword.cancel} 
                  handleClick={() => setIsReset(false)}
               />
            }

            <Modal
               dialogRef={dialogRef}
               messageUtils={{
                  message: (isReset) ? t.resetPassword.success : t.demo,
                  function: (isReset) ? 
                     () => setIsReset(false) : 
                     () => {
                        const demoAccounts = [
                           { email: "demo1@example.names.example", password: "123456" },
                           { email: "demo2@example.names.example", password: "123456" }
                        ];
                        handleSubmit(null, demoAccounts[Math.round(Math.random())]);
                     },
                  cancel: !isReset,
                  accept: true
               }}
            />
         </div>
      </section>
   );
};