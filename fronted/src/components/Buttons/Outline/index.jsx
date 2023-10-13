const BASE_BUTTON_CLASSES =
   "flex flex-col justify-center items-center h-10 rounded-3xl w-full";

export const Outline = ({ 
      label,
      isDisable
   }) => {

   const colorStyles = (isDisable) ? "border-onSurface/12 text-onSurface/38 dark:border-onSurface-dark/12 dark:text-onSurface-dark/38" : 
   "group-hover/outline:bg-primary/8 text-primary border-onSurface group-focus/outline:bg-primary/12 group-focus/outline:border-primary" + 
      "dark:group-hover/outline:bg-primary-dark/8 dark:text-primary-dark dark:border-onSurface-dark dark:group-focus/outline:bg-primary-dark/12 dark:group-focus/outline:border-primary-dark";

   return (
      <span 
         aria-disabled={isDisable}
         className={`${BASE_BUTTON_CLASSES} ${colorStyles} px-3 text-sm text-center font-medium border`}>
         {label}
      </span>
   );
};