/* eslint-disable react/prop-types */
export const DividerField = ({ label }) => {
   return (
      <button className="w-full h-12 p-4 inline-flex flex-row items-center justify-between outline-none border-b border-outline-variant focus:bg-onSurface/12 hover:bg-onSurface/8">
         {label}
         <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="24" height="24"/>
            <path d="M12.6 12L8 7.4L9.4 6L15.4 12L9.4 18L8 16.6L12.6 12Z" fill="#1C1B1F"/>
         </svg>
      </button>
   );
}