# Project Submission (Report):
---

### **1. Test Code Coverage**

* **Test Code Coverage:**
* **Status:** 100% (Target: >60%)
* **Tests:** Includes positive/negative tests for User, Cart, and Order controllers.
* **Evidence:**
> **[SCREENSHOT]**

![Test Coverage](images/Test_Coverage.png)

you can file the full report in [here](/starter_code/htmlReport/index.html)

---

### **2. Metrics, Dashboards, and Alerts**

* **Logging Strategy:**
* Structured logs implemented for (Event): `AddToCart`, `RemoveFromCart`, `FinishOrder`, `GetOrder`, and `CreateUser`.
* Status: `SUCCESS`, `FAILURE`, `UNAUTHORIZED`.

* **Splunk Integration:**
* **Dashboard:** Visualizes logs data and ability to filter.
![Splunk Search 1](images/Splunk_Search_1.png)
![Splunk Search 2](images/Splunk_Search_2.png)
![Splunk Search 3](images/Splunk_Search_3.png)
![Splunk Search 4](images/Splunk_Search_4.png)

* **Alert:** Configured to trigger on Unauthorized Status.
![Splunk Search 6](images/Splunk_Search_6.png)
![Splunk Search 5](images/Splunk_Search_5.png)

---

### **3. CI/CD (Jenkins)**

* **Pipeline Automation:**
* Automated the `Build`, `Test` and `Deployment` using Jenkins.
![Jenkins_Config_1](images/Jenkins_Config_1.png)
![Jenkins_Config_2](images/Jenkins_Config_2.png)
* **Status:** Build passes successfully.
![Jenkins_2](images/Jenkins_2.png)
![Jenkins_3](images/Jenkins_3.png)
![Jenkins_4](images/Jenkins_4.png)
![Jenkins_5](images/Jenkins_5.png)
You can see the full logs report for the process [here](ci_ci_log.txt)