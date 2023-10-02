import { withRouter } from "storybook-addon-react-router-v6";
import { Account } from ".";

export default {
   title: "Pages/Account",
   component: Account,
   tags: ["autodocs"],
   decorators: [withRouter],
   parameters: {
      layout: "fullscreen",
   }
};

export const Default = {};