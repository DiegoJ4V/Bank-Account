import { InputTypes } from "../../constants/InputType";
import { TextFieldTypes } from "../../constants/TextFieldType";
import { TextField } from "./TextField";

export default {
   title: "TextField",
   component: TextField,
   tags: ["autodocs"],
   argTypes: {
      type: {
         options: Object.keys(TextFieldTypes),
         mapping: TextFieldTypes,
         control: "radio"
      },
      inputType: {
         options: Object.keys(InputTypes),
         mapping: InputTypes,
         control: "radio"
      }
   }
};

const Template = ({ label, ...args }) => {
   return TextField({ label, ...args });
};

export const Default  = Template.bind({});
Default.args = {
   forWhat: "Base",
   supportiveText: "Text"
};