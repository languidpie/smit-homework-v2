/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'vinyl': {
          'black': '#1a1a1a',
          'groove': '#2d2d2d',
          'label': '#c9a227',
          'accent': '#e74c3c',
        },
        'bike': {
          'steel': '#4a5568',
          'chrome': '#a0aec0',
          'orange': '#ed8936',
        }
      }
    },
  },
  plugins: [],
}
