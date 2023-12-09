# Asset Management Tool

This application empowers users to efficiently manage their financial 
assets and monitor the growth of their wealth over time. It offers 
valuable insights for *anticipating future gains* and *assessing potential 
losses*, aiding users in **making informed investment decisions**. For people 
who actively engage in financial activities, this wealth tracker is 
**time-saving**, **accurate**, and **easy for use**. 

The development of such application holds my interest due to the
ever-evolving nature of the financial market. With new financial products
emerge daily, the need for effective asset management becomes increasingly 
evident. Keeping track of a diverse portfolio can be challenging, and 
recognizing this challenge, I am committed to creating a practical tool. 
This tool aims to offer real-world utility, alleviating the complexities 
of financial control and assisting individuals in reaching their 
financial objectives.

## User Stories
- As a user, I want to be able to add a new asset to my account and 
specify the investment date, interest rate, term to maturity, and principal.
- As a user, I want to be able to view a list of assets in my account.
- As a user, I want to be able to see the period-to-date interest and current interest earned.
- As a user, I want to be able to see a potential loss if I withdraw a premature asset.
- As a user, I want to be able to remove an expired/empty asset from my account.
- As a user, I want to be able to see the accumulated wealth in my account.
- As a user, I want to be prompted with the option to load data from file when the application starts and 
prompted with the option to save data to file when the application ends.

## Instructions for Grader
- You can generate the first required action related to the user story "adding multiple assets to an account" by 
navigating to the "Add New Asset" tab on the left side of the window, entering the relevant information for the new asset, 
and clicking the "Add" button to confirm your adding. You may then click the refresh icon in both the "Asset" and 
"Account" tabs to view the changes.
- You can generate the second required action related to the user story "removing an asset from my account" by 
navigating to the "Asset" tab, selecting an asset from the list and clicking the "Remove" button.
- You can locate my visual component by navigating to the "Account" tab, where a user avatar and a bar chart are displayed. 
You can also find the refresh button in the top right corner of the "Account" tab and beside the "Refresh" icon 
on the left panel in the "Asset" tab.
- You can save the state of my application by selecting the "Yes" button when prompted with the option to save data 
to a file upon closing the window.
- You can reload the state of my application by choosing the "My Account" button when prompted with the option to 
load data from a file when the application starts (the other option, "Start a New Account," will not load the data).

## Phase 4: Task 2
Thu Nov 23 12:17:14 PST 2023
Treasury Bill added to account.

Thu Nov 23 12:17:14 PST 2023
test added to account.

Thu Nov 23 12:17:14 PST 2023
future contract added to account.

Thu Nov 23 12:17:19 PST 2023
test removed from account.

Thu Nov 23 12:17:45 PST 2023
gic added to account.

Thu Nov 23 12:17:58 PST 2023
Changes saved to file.

## Phase 4: Task 3
There are a couple of refactoring options to improve my project if I have more time:

- First, I would remove the "Withdrawal" class, which only has two fields: the time of making the withdrawal and the amount of the withdrawal. Since I added the EventLog class at a later stage, which tracks the time of every action taken place, I could streamline the class design by removing the unnecessary class and just logging the event of withdrawal.

- Secondly, I would implement the Observer Design Pattern for the Tab classes. Specifically, since a change in the "Add New Asset" would result in updates in the "Asset" tab and the "Account" tab, I may consider making the "Add New Asset" extend a "Subject" abstract class and registering the "Asset" tab and the "Account" tab, which implement the "Observer" interface.

- Thirdly, I might reduce the coupling between Tabs, Account, and WalesUI classes. The UML diagram indicates that there is currently too much coupling between them.

