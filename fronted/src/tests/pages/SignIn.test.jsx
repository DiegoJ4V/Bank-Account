import { fireEvent, waitFor } from "@testing-library/react";
import { SignIn } from "../../pages/SignIn/SignIn";
import userEvent from "@testing-library/user-event";
import { expect, vi } from "vitest";
import * as auth from "../../pages/_services/auth";
import { customRender } from "../../utils/renderTest";

describe("SignIn page tests", () => {
   it("Should render SignIn page correctly", () => {  
      const page = customRender(<SignIn />);

      page.getByRole("heading");
      page.getByLabelText("Email");
      page.getByLabelText("Password");
      page.getByRole("button");
   });

   describe("After clicking", () => {
      it("If some value is not passed, should show the following error", async () => { 
         const page = customRender(<SignIn />);
         const button = page.getByRole("button");

         fireEvent.click(button);
   
         await waitFor(() => {
            expect(page.getByLabelText("Password")).toHaveAccessibleErrorMessage("Must not be empty");
            expect(page.getByLabelText("Email")).toHaveAccessibleErrorMessage("Must not be empty");
         });
      });

      it("If the credentials are wrong, should show the following error", async () => {
         const page = customRender(<SignIn />);
         const user = userEvent.setup();
         const spyLogin = vi.spyOn(auth, "login");

         const button = page.getByRole("button");
         const emailInput = page.getByLabelText("Email");
         const passwordInput = page.getByLabelText("Password");

         await user.type(emailInput, "error@user.com");
         await user.type(passwordInput, "1234");

         await user.click(button);

         await waitFor(() => {
            expect(spyLogin).toHaveBeenCalledTimes(1);
            expect(spyLogin).toHaveBeenLastCalledWith("error@user.com", "1234");
         });

         await waitFor(() => {
            expect(passwordInput).toHaveAccessibleErrorMessage("Incorrect authentication credentials");
            expect(emailInput).toHaveAccessibleErrorMessage("Incorrect authentication credentials");
         });
      });

      it("If the credentials are good, shouldn't show the following error", async () => {
         const page = customRender(<SignIn />);
         const user = userEvent.setup();
         const spyLogin = vi.spyOn(auth, "login");

         const button = page.getByRole("button");
         const emailInput = page.getByLabelText("Email");
         const passwordInput = page.getByLabelText("Password");

         await user.type(emailInput, "user@user.com");
         await user.type(passwordInput, "1234");

         await user.click(button);

         await waitFor(() => {
            expect(spyLogin).toHaveBeenCalledTimes(1);
            expect(spyLogin).toHaveBeenCalledWith("user@user.com", "1234");
         });

         await waitFor(() => {
            expect(passwordInput).not.toHaveAccessibleErrorMessage("Incorrect authentication credentials");
            expect(emailInput).not.toHaveAccessibleErrorMessage("Incorrect authentication credentials");
         });
      });
   });
});