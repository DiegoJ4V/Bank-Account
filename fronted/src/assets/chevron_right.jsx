/**
 * JSX icon to indicate that something happen when you make a click
 * @param {object} props 
 * @param {string} props.fillClass string containing all tailwind class
 * @returns
 */
export const ChevronIcon = ({ fillClass }) => (
   <svg 
      className={fillClass} 
      aria-hidden="true"
      focusable="false"
      width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect width="24" height="24" fill="none"/>
      <path d="M12.6 12L8 7.4L9.4 6L15.4 12L9.4 18L8 16.6L12.6 12Z"/>
   </svg>
);

