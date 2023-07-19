/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    opacity: {
      "12" : "12%",
      "38" : "38%",
      "80" : "80%",
      "120" : "120%"
    },
    colors: {
      "transparent" : "transparent",

      "primary" : "#2563EB",
      "onPrimary" : "#EFF6FF",
      "primary-container" : "#DBEAFE",
      "onPrimary-container" : "#1E3A8A",
      
      "secondary-container" : "#DEE4F8",
      "onSecondary-container" : "#191B2B",
      
      "outline" : "#74757E",
      "outline-variant" : "#C4C4C7",

      "surface" : "#F7F8FF",
      "surface-container" : "#EDF0F7",
      "surface-container-highest": "#E0E1E9",
      "onSurface" : "#1B1C1F",
      "onSurface-variant" : "#45454E",
    },
    extend: {},
  },
  plugins: [],
}

