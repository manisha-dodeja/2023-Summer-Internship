# 2023-Summer-Internship
# ETL Memory Prediction

## Dependencies

JDK 15

Python 3.11

mongodb-driver 3.12.6 (In case we want to read the job metrics from etl_dispatcher database on  MongoDB server instead of athena_local DB on SQL server )

ca-database-utils jar (uses JDBC driver to connect to SQL Server)

Flask

gson 2.10.1




We have two applications within the same project.
Following two configurations are required to set up.
![Tomcat](./supporting-documents/images/Tomcat.png)
![Model](./supporting-documents/images/Model.png)

And the project structure should look like the following
![Model](./supporting-documents/images/JDk.png)
![Model](./supporting-documents/images/Python.png)

## System Workflow
### Python Model
#### app.py
It’s a linear regression model.  
A post request is received by this model (predict method) where the request payload is a vector of independent variables (Number of Transactions and Lots)
This method checks if there exits a model if it turns out to be false, then build method is called.
Then it creates a linear regression object and initializes the model coefficients and intercept to be equal to the ones saved by the build method.

##### Build Method
    It hits the java endpoint (api/modeldata/java-endpoint) to receive a Json payload which has training data.
    It cleans the data and creates an x and y data frame from the training data and fits the model.
    It saves the coefficients and intercepts on a Json file.
    

### Java Application

#### api/modeldata/java-endpoint  (ModelData.java)

It fetches the data (that makes up the training for the model) from the file, it was saved to during the earlier run.
Or if it’s the very run, it fetches the data from SQL Server DB.
It establishes connection with the athena_local DB, and fetches all rows (Columns: Memory, Cores,AccountID)  from AccountETLparameters table and stores in an HashMap called InputData.

Fetches count of Transactions associated with each account in InputData  (Transaction.java) into a HashMap called TransactionData and stores them on transc.json.
Fetches count of Lots and Security associated with each account in InputData (SecurityMap.java) and stores them on lotsec.json
Fetches count of Basis associated with each account in InputData(BasisWS.java) and stores them on Basis.json

Creates a Json object with the above data, and sends it as a response to the request made by the build method of app.py.



#### Transaction.java
It establishes connection with LM DB and fetches count of transactions for each account into the HashSet (function parameter) , saves it into transc.json

#### SecurityMap.java
It establishes connection with LM DB and fetches count of lots and securities for each account into the HashSet (function parameter) , saves it into lotsec.json

#### BasisWS.java (NOTE: This isn’t being currently used, but can be used as one of the independent variables for estimating Cores)
Sends a post request to http://dev-aws1-service-tomcats2-app200.arbfund.com:8084/basis-ws/v2/bases/map , receives response in the following format Map<String, int[]>, counts the length of int[] to have count of basis. Saves it into Basis.json

#### SampleEndpoint.java
This is the endpoint Athena team would be directly using and sending AccountID  as a query parameter
api/athena/endpoint?account=1457

This fetches all the independent variables associated to that account , Transactios and Lots count in our case and sends it to @app.route('/predict', methods=['POST']) as listed at the very start of this document.


###  Diagram
Following diagram gives a pictorial representation of communication that happens with in the system.
![Tomcat](./supporting-documents/images/Flow Diagram.png)
