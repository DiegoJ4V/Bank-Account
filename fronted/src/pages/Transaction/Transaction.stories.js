import { withRouter } from "storybook-addon-react-router-v6";
import { Transaction } from ".";
import { rest } from "msw";
import { userEvent, waitFor, within } from "@storybook/testing-library";
import { getTraduction } from "../../utils/getTraduction";
import { Traduction } from "../../constants/Traduction";
import { expect } from "@storybook/jest";

export default {
   title: "Pages/Transaction",
   component: Transaction,
   decorators: [withRouter],
   parameters: {
      layout: "fullscreen",
      backgrounds: {
         default: "dark-blue",
         values: [
            { name: "blue", value: "rgb(247 248 255)"},
            { name: "dark-blue", value: "rgb(18 18 24)"}
         ]
      },
   }
};

const API = "http://localhost:8090/transactions/save";

export const Default = {};

export const Load = {
   play: async ({ canvasElement, step }) => { 
      const canvas = within(canvasElement);
      const t = getTraduction(Traduction.TRANSACTION_PAGE);
      const tType = getTraduction(Traduction.TRANSACTION_TYPE);
      
      const typeInput = canvas.getByLabelText(t.labels[0]);
      const amountInput = canvas.getByLabelText(t.labels[1]);
      const transferInput = canvas.getByLabelText(t.labels[2]);

      const acceptButton = canvas.getByRole("button", { name: t.accept });

      await step("Enter and submit necessary data", async () => {
         await userEvent.click(typeInput);
         await userEvent.click(canvas.getByText(tType.wireTransfer));
         await userEvent.type(amountInput, "2321313312.32");
         await userEvent.type(transferInput, "4234");
         
         await userEvent.click(canvas.getByRole("button", { name: t.accept }));
      });

      await step("Initialize load state", async () => {
         await waitFor(async () => {
            await expect(typeInput).toBeDisabled();
            await expect(amountInput).toBeDisabled();
            await expect(transferInput).toBeDisabled();
            await expect(acceptButton).toBeDisabled();

            await expect(canvas.getByRole("progressbar")).toBeInTheDocument();
         });
      });
   },
   parameters: {
      msw: [
         rest.post(API, (req, res, ctx) => {
            return res(ctx.delay("infinite"));
         }),
      ],
   },
};

export const Error = {
   play: async ({ canvasElement, step }) => { 
      const canvas = within(canvasElement);
      const t = getTraduction(Traduction.TRANSACTION_PAGE);
      const tType = getTraduction(Traduction.TRANSACTION_TYPE);
      
      const typeInput = canvas.getByLabelText(t.labels[0]);
      const amountInput = canvas.getByLabelText(t.labels[1]);
      const transferInput = canvas.getByLabelText(t.labels[2]);

      const acceptButton = canvas.getByRole("button", { name: t.accept });

      await step("Enter and submit necessary data", async () => {
         await userEvent.dblClick(typeInput,);
         await userEvent.click(canvas.getByText(tType.deposit));
         await userEvent.type(amountInput, "2321313312.32");
         
         await userEvent.click(canvas.getByRole("button", { name: t.accept }));
      });

      await step("Initialize load state", async () => {
         await waitFor(async () => {
            await expect(typeInput).toBeDisabled();
            await expect(amountInput).toBeDisabled();
            await expect(transferInput).toBeDisabled();
            await expect(acceptButton).toBeDisabled();
   
            await expect(canvas.getByRole("progressbar")).toBeInTheDocument();
         });
      });

      await step("Initialize fail state", () => {
         setTimeout(async () => {
            await waitFor(async () => {
               await expect(amountInput).toBeInvalid();
               await expect(transferInput).toBeDisabled();
            });
         }, 900);
      });

   },
   parameters: {
      msw: [
         rest.post(API, (req, res, ctx) => {
            return res(ctx.status(400), ctx.json({
               amount: "Not enough balance",
            }));
         }),
      ],
   },
};

export const Successful = {
   play: async ({ canvasElement, step }) => { 
      const canvas = within(canvasElement);
      const t = getTraduction(Traduction.TRANSACTION_PAGE);
      const tType = getTraduction(Traduction.TRANSACTION_TYPE);
      
      const typeInput = canvas.getByLabelText(t.labels[0]);
      const amountInput = canvas.getByLabelText(t.labels[1]);
      const transferInput = canvas.getByLabelText(t.labels[2]);

      const acceptButton = canvas.getByRole("button", { name: t.accept });

      await step("Enter and submit necessary data", async () => {
         await userEvent.click(typeInput);
         await userEvent.click(canvas.getByText(tType.wireTransfer));
         await userEvent.type(amountInput, "2321313312.32");
         await userEvent.type(transferInput, "4234");

         await userEvent.click(canvas.getByRole("button", { name: t.accept }));
      });

      await step("Initialize load state", async () => {
         await waitFor(async () => {
            await expect(typeInput).toBeDisabled();
            await expect(amountInput).toBeDisabled();
            await expect(transferInput).toBeDisabled();
            await expect(acceptButton).toBeDisabled();
   
            await expect(canvas.getByRole("progressbar")).toBeInTheDocument();
         });
      });

      await step("See successful response", async () => {
         setTimeout(async () => {
            await expect(await canvas.findByText("Transaction made successfully")).toBeInTheDocument();
         }, 1500);
      });
   },
   parameters: {
      msw: [
         rest.post(API, (req, res, ctx) => {
            return res(ctx.status(200), ctx.delay(1500), ctx.text("Transaction made successfully"));
         }),
      ],
   },
};
