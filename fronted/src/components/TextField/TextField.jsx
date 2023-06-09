/* eslint-disable react/prop-types */
import { useState } from "react";
import { TextFieldTypes } from "../../constants/TextFieldType";
import { InputTypes } from "../../constants/InputType";
import { Menu } from "../Menu/Menu";
import { useOutsideClick } from "../../hooks/useOutsideClick";
import "./TextField.css";

export const TextField = ({
   forWhat,
   type = TextFieldTypes.Default,
   inputType = InputTypes.Text,
   supportiveText = ""
}) => {

   const [isClicked, setIsClicked] = useState(false);
   const [hasText, setHasText] = useState(false);
   const [value, setValue] = useState("");
   
   const handleClick = () => {
      setIsClicked(true);
   };
   
   const handleClickOutside = () => {
      setIsClicked(false);
   };

   const ref = useOutsideClick(handleClickOutside);

   const onClear = () => setValue("");

   const transactionTypes = ["Deposit", "Online payment", "Wire Transfer"];
   const notMenu = type !== TextFieldTypes.Menu;
   
   return (
      <div ref={ref} className="inline-flex flex-col w-full">
         <label htmlFor={forWhat} className={`bg-transparent w-fit block absolute origin-top-left z-10 font-sans font-normal text-base cursor-text
         ${isClicked ? "label--position--click text-primary" : `label--position--base ${(type === TextFieldTypes.Search && !hasText) && "ml-6"}`}
         ${(hasText || inputType === InputTypes.Date) ? "label--position--click text-onSurface" : "text-onSurface-variant"}`}>
            {forWhat}
         </label>
            
         <div className={`inline-flex relative items-center rounded outline font-sans font-normal text-base cursor-text caret-primary
         ${isClicked ? "outline-2 outline-primary"  : "outline-1 outline-outline hover:outline-onSurface"}`}>

            {type === TextFieldTypes.Search && <svg className="ml-3" width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
               <path d="M19.6 21L13.3 14.7C12.8 15.1 12.225 15.4167 11.575 15.65C10.925 15.8833 10.2333 16 9.5 16C7.68333 16 6.14583 15.3708 4.8875 14.1125C3.62917 12.8542 3 11.3167 3 9.5C3 7.68333 3.62917 6.14583 4.8875 4.8875C6.14583 3.62917 7.68333 3 9.5 3C11.3167 3 12.8542 3.62917 14.1125 4.8875C15.3708 6.14583 16 7.68333 16 9.5C16 10.2333 15.8833 10.925 15.65 11.575C15.4167 12.225 15.1 12.8 14.7 13.3L21 19.6L19.6 21ZM9.5 14C10.75 14 11.8125 13.5625 12.6875 12.6875C13.5625 11.8125 14 10.75 14 9.5C14 8.25 13.5625 7.1875 12.6875 6.3125C11.8125 5.4375 10.75 5 9.5 5C8.25 5 7.1875 5.4375 6.3125 6.3125C5.4375 7.1875 5 8.25 5 9.5C5 10.75 5.4375 11.8125 6.3125 12.6875C7.1875 13.5625 8.25 14 9.5 14Z" fill="#45454E"/>
            </svg>}

            <input 
               id={forWhat}
               value={value}
               className={`w-full h-14 p-4 bg-transparent outline-none text-onSurface`}
               type={inputType.description}
               autoComplete="off"
               onClick={() => {
                  if (inputType !== InputTypes.Date) {
                     handleClick();
                  }
               }}
               onFocus={() => {
                  if (notMenu) { 
                     setIsClicked(true)
                  } else {
                     handleClick();
                     onClear();
                  }
               }}
               onBlur={() => {
                  if (notMenu) {
                     setIsClicked(false)
                  } else setHasText(false) 
               }}
               onChange={(e) => setHasText(e.target.value)}
               onInput={(e) => setValue(e.target.value)}
            />

            {(notMenu && inputType !== InputTypes.Date) && <svg 
                  className={`mr-3 ${(isClicked || hasText) ? "fill-onSurface-variant cursor-pointer" : "fill-none"}`} width="24"  height="24"  viewBox="0 0 24 24"  fill="none"  xmlns="http://www.w3.org/2000/svg"
                  aria-hidden="true"
                  focusable="false"
                  onClick={() => {
                     onClear();
                     setHasText(false);
                  }}
               >
               <path d="M8.4 17L12 13.4L15.6 17L17 15.6L13.4 12L17 8.4L15.6 7L12 10.6L8.4 7L7 8.4L10.6 12L7 15.6L8.4 17ZM12 22C10.6167 22 9.31667 21.7375 8.1 21.2125C6.88333 20.6875 5.825 19.975 4.925 19.075C4.025 18.175 3.3125 17.1167 2.7875 15.9C2.2625 14.6833 2 13.3833 2 12C2 10.6167 2.2625 9.31667 2.7875 8.1C3.3125 6.88333 4.025 5.825 4.925 4.925C5.825 4.025 6.88333 3.3125 8.1 2.7875C9.31667 2.2625 10.6167 2 12 2C13.3833 2 14.6833 2.2625 15.9 2.7875C17.1167 3.3125 18.175 4.025 19.075 4.925C19.975 5.825 20.6875 6.88333 21.2125 8.1C21.7375 9.31667 22 10.6167 22 12C22 13.3833 21.7375 14.6833 21.2125 15.9C20.6875 17.1167 19.975 18.175 19.075 19.075C18.175 19.975 17.1167 20.6875 15.9 21.2125C14.6833 21.7375 13.3833 22 12 22ZM12 20C14.2333 20 16.125 19.225 17.675 17.675C19.225 16.125 20 14.2333 20 12C20 9.76667 19.225 7.875 17.675 6.325C16.125 4.775 14.2333 4 12 4C9.76667 4 7.875 4.775 6.325 6.325C4.775 7.875 4 9.76667 4 12C4 14.2333 4.775 16.125 6.325 17.675C7.875 19.225 9.76667 20 12 20Z"/>
            </svg>}
            
            {(!notMenu && !isClicked) && <div className="bg-transparent flex justify-center items-center w-6 h-6 mr-3">
               <svg width="10" height="5" viewBox="0 0 10 5" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M5 5L0 0H10L5 5Z" fill="#1B1C1F"/>
               </svg>
            </div>}
            {(!notMenu  && isClicked) && <div className="bg-transparent flex justify-center items-center w-6 h-6 mr-3">
               <svg width="10" height="5" viewBox="0 0 10 5" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M0 5L5 0L10 5H0Z" fill="#2563EB"/>
               </svg>
            </div>}
         </div>
         {(supportiveText && notMenu) && <p className="ml-4 mt-1 text-onSurface-variant text-sm">{supportiveText}</p>}
         
         {(type === TextFieldTypes.Menu && isClicked)  && <Menu 
            text={value} 
            transactions={transactionTypes}
            setValue={setValue}
            setHasText={setHasText}
            handleClickOutside={handleClickOutside}
            setIsClicked={setIsClicked}
         />}
      </div>
   );
};
