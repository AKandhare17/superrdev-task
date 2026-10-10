# SuperrDev Task Tracker — Patch Notes

## 1. SQL Search and Status Filtering Bug

**File:** `backend/src/main/java/com/internal/tasktracker/TaskRepository.java`

**Issue:** Selecting a specific status, such as OPEN, returned tasks with other statuses.

**Root Cause:** The SQL query mixed `AND` and `OR` without proper parentheses. Due to SQL operator precedence, the search, archive, and status conditions were not applied correctly.

**Fix:** Grouped the title and description search conditions using parentheses so the filters work as intended.

## 2. Unnecessary Backend Request Delay

**File:** `backend/src/main/java/com/internal/tasktracker/TaskController.java`

**Issue:** Search requests took approximately 800–1100 ms during local testing.

**Root Cause:** The controller used `Thread.sleep()` to introduce an artificial delay based on the search query.

**Fix:** Removed the artificial delay and related complexity-score logic. After the change, local API response times dropped to approximately 25–35 ms.

## 3. Search Pagination Bug

**File:** `frontend/src/App.jsx`

**Issue:** Searching while on page 2 could display no results even when matching tasks existed.

**Root Cause:** Changing the search query did not reset the current page.

**Fix:** Reset the page to 1 whenever the search query changes. Verified that the API request uses page 1 after a new search.

## 4. Invalid Pagination Error

**File:** `backend/src/main/java/com/internal/tasktracker/TaskController.java`

**Issue:** Requests using `page=0` or `page=-1` returned HTTP 500 errors.

**Root Cause:** Pagination indexes were calculated before validating the input.

**Fix:** Added validation for page and page-size parameters before pagination. Invalid values return HTTP 400 Bad Request.

## What I Chose Not to Change

I avoided redesigning the UI or making major architectural changes to keep the patch focused. Invalid-status API handling remains a possible improvement.

## Biggest Remaining Risk

The backend retrieves all matching tasks before paginating in memory. Database-level pagination and automated tests would improve scalability and reliability.

## AI and Tools Used

I used ChatGPT to help understand the code and evaluate possible fixes. I reproduced issues using the application, browser Network tools, and backend logs, then reviewed and tested the changes locally.
