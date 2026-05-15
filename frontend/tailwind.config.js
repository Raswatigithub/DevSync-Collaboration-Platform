/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        ink: "#17202a",
        mint: "#16a085",
        coral: "#e76f51",
        amber: "#f4a261"
      }
    },
  },
  plugins: [],
};
