/**
 * JSX icon to represent something with the home page
 * @param {object} props 
 * @param {string} props.fillClass string containing all tailwind class
 * @returns
 */
export const HomeIcon = ({ fillClass }) => (
   <svg 
      className={fillClass}
      aria-hidden="true"
      focusable="false"
      width="16" height="18" viewBox="0 0 16 18" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M2 16H5V10H11V16H14V7L8 2.5L2 7V16ZM0 18V6L8 0L16 6V18H9V12H7V18H0Z"/>
   </svg>
);

