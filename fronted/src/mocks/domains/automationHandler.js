import { rest } from "msw";
import automations from "../fixtures/automations.json";

const API = "http://localhost:8090/automations";

export const automationHandler = [
   rest.get(`${API}/account`, (req, res, ctx) => {
      const idAccount = req.url.searchParams.get("id");

      if (idAccount == 238589851) {
         return res(ctx.json(automations));
      }

      return res(ctx.status(404));
   }),

   rest.put(`${API}/status`, (req, res, ctx) => {
      const id = req.url.searchParams.get("id");

      if (id == 1) {
         return res(ctx.status(200));
      }

      return res(ctx.status(404));
   }),

   rest.post(`${API}/save`, async (req, res, ctx) => {
      const { idTransferAccount } = await req.json();

      if (idTransferAccount == 124124) {
         return res(ctx.status(404), ctx.json({
            desc: "Account not found 124124"
         }));
      } else {
         return res(ctx.status(200), ctx.json({
            "idAutomation": 238589851,
            "name": "New automation",
            "amount": 2000.00,
            "idTransferAccount": 2132,
            "hoursToNextExecution": 1,
            "executionTime": "2024-07-15T17:52:54.894278577",
            "status": true
         }));
      }
   })
];