/**
 * JSX icon to indicate that something is not displayed
 * @param {object} props 
 * @param {string} props.fillClass string containing all tailwind class
 * @returns 
 */
export const ArrowDownIcon = ({ fillClass }) => (
   <svg 
      className={fillClass} 
      aria-hidden="true"
      focusable="false"
      width="10" height="5" viewBox="0 0 10 5" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M5 5L0 0H10L5 5Z" />
   </svg>
);

