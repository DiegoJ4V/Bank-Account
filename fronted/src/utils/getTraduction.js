import { Traduction } from "../constants/Traduction";
import modal from "../i18n/modal.json";
import navbar from "../i18n/navbar.json";
import loader from "../i18n/loader.json";
import transactionType from "../i18n/transaction-types.json";
import auth from "../i18n/auth.json";
import accountPage from "../i18n/account-page.json";
import automationPage from "../i18n/automation-page.json";
import automationsPage from "../i18n/automations-page.json";
import updateAutomationPage from "../i18n/update-automation-page.json";
import homePage from "../i18n/home-page.json";
import signInPage from "../i18n/sign-in-page.json";
import signUpPage from "../i18n/sign-up-page.json";
import transactionPage from "../i18n/transaction-page.json";
import transactionsPage from "../i18n/transactions-page.json";
import tokenRegisterPage from "../i18n/token-register-page.json";
import tokenEmailPage from "../i18n/token-email-page.json";
import savePasswordPage from "../i18n/save-password-page.json";

/** @type {string} */
const language = localStorage.getItem("language") ?? navigator.language;

/**
 * A function will return a traduction according to the Traduction constant and the language saved or prefer
 * @param {Traduction} wantedTranslation The Traduction that you want to use
 * @returns {object} The object with the traduction values 
 */
export const getTraduction = (wantedTranslation) => {
   /** @type {string} */
   const availableLanguage = language.includes("es") ? "es" : "en";
   
   switch (wantedTranslation) {
      case Traduction.MODAL:
         return modal[availableLanguage];
      case Traduction.NAVBAR:
         return navbar[availableLanguage];
      case Traduction.LOADER:
         return loader[availableLanguage];
      case Traduction.TRANSACTION_TYPE:
         return transactionType[availableLanguage];
      case Traduction.LOGIN:
         return auth[availableLanguage];
      case Traduction.ACCOUNT_PAGE: 
         return accountPage[availableLanguage];
      case Traduction.AUTOMATION_PAGE: 
         return automationPage[availableLanguage];
      case Traduction.AUTOMATIONS_PAGE: 
         return automationsPage[availableLanguage];
      case Traduction.UPDATE_AUTOMATION_PAGE: 
         return updateAutomationPage[availableLanguage];
      case Traduction.HOME_PAGE: 
         return homePage[availableLanguage];
      case Traduction.SIGN_IN_PAGE: 
         return signInPage[availableLanguage];
      case Traduction.SIGN_UP_PAGE: 
         return signUpPage[availableLanguage];
      case Traduction.TRANSACTION_PAGE: 
         return transactionPage[availableLanguage];
      case Traduction.TRANSACTIONS_PAGE: 
         return transactionsPage[availableLanguage];
      case Traduction.TOKEN_REGISTER:
         return tokenRegisterPage[availableLanguage];
      case Traduction.TOKEN_EMAIL:
         return tokenEmailPage[availableLanguage];
      case Traduction.SAVE_PASSWORD:
         return savePasswordPage[availableLanguage];
   }
};