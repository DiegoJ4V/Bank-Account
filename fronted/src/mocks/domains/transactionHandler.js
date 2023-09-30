import { rest } from "msw";
import transactions from "../fixtures/transactions.json";

const API = "http://localhost:8090/transactions";

export const transactionHandler = [
   rest.get(`${API}/account`, (req, res, ctx) => {
      const idAccount = req.url.searchParams.get("id");
      
      if (idAccount == 238589851) {
         return res(ctx.json(transactions));
      } else {
         return res(ctx.status(404));
      }
   }),

   rest.get(`${API}/name`, (req, res, ctx) => {
      const name = req.url.searchParams.get("name");

      if (name === "new") {
         return res(ctx.json({
            "content": [
               {
                  "idTransaction": 1,
                  "idTransferAccount": 654,
                  "receiverName": "New",
                  "transactionAmount": 120.00,
                  "transactionType": "WIRE_TRANSFER",
                  "transactionTimestamp": "2023-06-26T21:02:13.374219",
                  "isAutomated": false
               }, {
                  "idTransaction": 2,
                  "idTransferAccount": 654,
                  "receiverName": "New",
                  "transactionAmount": 120.00,
                  "transactionType": "WIRE_TRANSFER",
                  "transactionTimestamp": "2023-06-26T21:02:13.374219",
                  "isAutomated": false
               }
            ],
            "pageable": {
               "sort": {
                  "empty": true,
                  "unsorted": true,
                  "sorted": false
               },
               "offset": 0,
               "pageNumber": 0,
               "pageSize": 10,
               "paged": true,
               "unpaged": false
            },
            "totalPages": 1,
            "totalElements": 1,
            "last": true,
            "size": 10,
            "number": 0,
            "sort": {
               "empty": true,
               "unsorted": true,
               "sorted": false
            },
            "numberOfElements": 1,
            "first": true,
            "empty": false
         }));
      } 

      return res(ctx.status(404));
   }),

   rest.get(`${API}/date`, (req, res, ctx) => {
      const year = req.url.searchParams.get("year");

      if (year === "2023") {
         return res(ctx.json({
            "content": [
               {
                  "idTransaction": 1,
                  "idTransferAccount": 654,
                  "receiverName": "New",
                  "transactionAmount": 120.00,
                  "transactionType": "WIRE_TRANSFER",
                  "transactionTimestamp": "2023-06-26T21:02:13.374219",
                  "isAutomated": false
               }, {
                  "idTransaction": 2,
                  "idTransferAccount": 654,
                  "receiverName": "New",
                  "transactionAmount": 120.00,
                  "transactionType": "WIRE_TRANSFER",
                  "transactionTimestamp": "2023-06-26T21:02:13.374219",
                  "isAutomated": false
               }
            ],
            "pageable": {
               "sort": {
                  "empty": true,
                  "unsorted": true,
                  "sorted": false
               },
               "offset": 0,
               "pageNumber": 0,
               "pageSize": 10,
               "paged": true,
               "unpaged": false
            },
            "totalPages": 1,
            "totalElements": 1,
            "last": true,
            "size": 10,
            "number": 0,
            "sort": {
               "empty": true,
               "unsorted": true,
               "sorted": false
            },
            "numberOfElements": 1,
            "first": true,
            "empty": false
         }));
      } 

      return res(ctx.status(404));
   }),

   rest.post(`${API}/save`, async (req, res, ctx) => {
      const { idTransferAccount } = await req.json();

      if (idTransferAccount == 124124) {
         return res(ctx.status(404), ctx.json({
            amount: "Not enough balance"
         }));
      }

      return res(ctx.status(200), ctx.text("Transaction made successfully"));
   })
];
