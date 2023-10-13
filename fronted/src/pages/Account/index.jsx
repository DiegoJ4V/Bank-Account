import { useState } from "react";
import { DividerField } from "../../components/Divider/DividerField";
import { Navbar } from "../../components/Navbar";
import { Switch } from "../../components/Switch";
import { Page } from "../../constants/Page";
import { changeEmail, changeName, changePassword } from "../_services/auth";
import { useNavigate } from "react-router-dom";
import { getTraduction } from "../../utils/getTraduction";
import { Traduction } from "../../constants/Traduction";
import { SEO } from "../../utils/SEO";

export const Account = () => {
   const [error, setError] = useState({});
   const [successMessage, setSuccessMessage] = useState("");
   const navigate = useNavigate();

   const t = getTraduction(Traduction.ACCOUNT_PAGE);
   const { accountName, idAccount } = JSON.parse(localStorage.getItem("account"));

   const handleMethods = async (firstParameter, secondParameter, changeFunction) => {
      try {
         const data = await changeFunction(firstParameter, secondParameter);
         setSuccessMessage(data);
      } catch (e) {
         const message = JSON.parse(e.message);
         setError(message);
      }
   };

   const changeLanguage = () => {
      const languageToChange = localStorage.getItem("language") ?? navigator.language;

      if (languageToChange.includes("es")) {
         localStorage.setItem("language", "en");
      } else {
         localStorage.setItem("language", "es");
      }

      globalThis.location.reload();
   };

   const handleName =  (newName, password) => {
      handleMethods(newName, password, changeName);
   };

   const handlePassword =  (oldPassword, newPassword) => {
      handleMethods(oldPassword, newPassword, changePassword);
   };

   const handleEmail =  (newEmail, password) => {
      handleMethods(newEmail, password, changeEmail);
   };

   const closeSession = () => {
      localStorage.removeItem("account");
      localStorage.removeItem("token");
      navigate("/sign-in");
   };

   return (
      <section className="flex flex-row-reverse w-full justify-end bg-surface dark:bg-surface-dark">
         <SEO title={t.seo.title} description={t.seo.description} />
         <div className="w-full bg-white border border-outline-variant md:rounded-2xl  md:mx-6 md:my-4 dark:bg-black dark:border-outline-variant-dark">
            <h1 className="ml-4 md:ml-6 mt-8 text-4xl font-sans font-bold text-onSurface dark:text-onSurface-dark">{accountName}</h1>
            <p className="ml-4 md:ml-6 mt-3 text-base font-sans font-normal text-onSurface dark:text-onSurface-dark">{t.accountNumber}: {idAccount}</p>

            <div className="w-full px-4 py-2 mt-8 border-b border-outline-variant dark:border-outline-variant-dark">
               <Switch 
                  label={t.dark}
                  isDisable={false}
                  checked={false}
               />
            </div>
            <DividerField 
               label= {t.change.language.label}
               modalUtils={{
                  messageUtils: {
                     message: t.change.language.message,
                     changeLanguage
                  }
               }}
            />
            <DividerField 
               label={t.change.name.label}
               modalUtils={{
                  formUtils: {
                     inputs: t.change.name.inputs,
                     handle: handleName,
                     successMessage,
                     setSuccessMessage,
                     setError,
                     errorParameters: {
                        first: error?.name ?? "",
                        second: error?.newPassword ?? ""
                     }
                  }
               }}
            />
            <DividerField 
               label={t.change.password.label}
               modalUtils={{
                  formUtils: {
                     inputs: t.change.password.inputs,
                     handle: handlePassword,
                     successMessage,
                     setSuccessMessage,
                     setError,
                     errorParameters: {
                        first: error?.oldPassword ?? "",
                        second: error?.newPassword ?? ""
                     }
                  }
               }}
            />
            <DividerField 
               label={t.change.email.label}
               modalUtils={{
                  formUtils: {
                     inputs: t.change.email.inputs,
                     handle: handleEmail,
                     successMessage,
                     setSuccessMessage,
                     setError,
                     errorParameters: {
                        first: error?.email ?? "",
                        second: error?.newPassword ?? ""
                     },
                     closeSession
                  }
               }}
            />
            <DividerField 
               label= {t.logout.label}
               modalUtils={{
                  messageUtils: {
                     message: t.logout.message,
                     closeSession
                  }
               }}
            />
         </div>
         <Navbar page={Page.Account} />
      </section>
   );
};