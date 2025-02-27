# Case Study - PDF Comparison

<p align="center">
    <img src="screenshots/main.png" alt="Main Information" width="800" height="500">
</p>

### 📖 Information

<ul style="list-style-type:disc">
   <li>
      This project provides a <b>Spring Boot API</b> for <b>PDF Comparison</b>, allowing users to compare two PDF documents and download a resulting comparison report in <b>PDF format</b>.
   </li>
   <li>
      <b>PDF Comparison API:</b>
      <ul>
         <li>
            <b>Endpoint:</b> <code>/api/v1/pdf/compare</code>
         </li>
         <li>
            <b>HTTP Method:</b> POST
         </li>
         <li>
            <b>Consumes:</b> <code>multipart/form-data</code>
         </li>
         <li>
            <b>Parameters:</b>
            <ul>
               <li>
                  <b>file1:</b> First PDF file (<b>required</b>)
               </li>
               <li>
                  <b>file2:</b> Second PDF file (<b>required</b>)
               </li>
               <li>
                  <b>isMultiple:</b> Boolean flag (<b>optional</b>, default is <code>false</code>) indicating whether to perform multiple comparisons.
               </li>
            </ul>
         </li>
         <li>
            <b>Response:</b> Returns a PDF file (<code>comparison.pdf</code>) containing the comparison results.
         </li>
         <li>
            <b>Example Request:</b>
            <ul> 
                <li><code>POST /api/v1/pdf/compare</code>
            with two PDF files and an optional <code>isMultiple</code> flag.
         </li>
      </ul>
   </li>
   <li>
      <b>Implementation Details:</b>
      <ul>
         <li>
            Utilizes a dedicated <b>PDFComparisonService</b> to process and compare the contents of the uploaded PDF files.
         </li>
         <li>
            The service generates a comparison report as a PDF document, highlighting differences and similarities.
         </li>
         <li>
            Errors during the comparison process are managed via exception handling, ensuring reliable API responses.
         </li>
      </ul>
   </li>
   <li>
      <b>Downloadable Output:</b>
      <ul>
         <li>
            The response sets <b>Content-Type</b> to <code>application/pdf</code> and uses <b>Content-Disposition</b> to prompt the download of the file named <code>comparison.pdf</code>.
         </li>
      </ul>
   </li>
</ul>


### Explore Rest APIs

Endpoints Summary
<table style="width:100%;">
    <tr>
        <th>Method</th>
        <th>Url</th>
        <th>Description</th>
        <th>Request Body</th>
        <th>Path Variable</th>
        <th>Response</th>
    </tr>
    <tr>
        <td>POST</td>
        <td>/api/v1/pdf/compare</td>
        <td>Compare two PDF files and return a comparison report</td>
        <td>
            <ul>
                <li><b>file1:</b> First PDF file (required)</li>
                <li><b>file2:</b> Second PDF file (required)</li>
                <li><b>isMultiple:</b> Boolean flag (optional, default is false)</li>
            </ul>
        </td>
        <td>None</td>
        <td>PDF file (comparison.pdf)</td>
    </tr>
</table>




### Technologies

---
- Java 21
- Spring Boot 3.0
- Restful API
- Open Api (Swagger)
- Maven
- Junit5
- Mockito
- Integration Tests
- Docker
- Docker Compose
- CI/CD (Github Actions)
- Postman
- Prometheus
- Grafana
- Kubernetes
- JaCoCo (Test Report)
- Alert Manager

### Postman

```
Import postman collection under postman_collection folder
```


### Prerequisites

#### Define Variable in .env file

```
GF_SMTP_ENABLED=true
GF_SMTP_HOST=smtp.gmail.com:587
GF_SMTP_USER={your_gmail_email}
GF_SMTP_PASSWORD={gmail_authentication_password}
GF_SMTP_SKIP_VERIFY=true
GF_SMTP_FROM_ADDRESS={your_gmail_email}


ALERT_RESOLVE_TIMEOUT=5m
SMTP_SMARTHOST=smtp.gmail.com:587
SMTP_FROM={your_gmail_email}
SMTP_AUTH_USERNAME={your_gmail_email}
SMTP_AUTH_PASSWORD={gmail_authentication_password}
SMTP_REQUIRE_TLS=true
ALERT_EMAIL_TO={your_gmail_email}
```

### Open Api (Swagger)

```
http://localhost:3333/swagger-ui/index.html
```

---

### JaCoCo (Test Report)

After the command named `mvn clean install` completes, the JaCoCo report will be available at:
```
target/site/jacoco/index.html
```
Navigate to the `target/site/jacoco/` directory.

Open the `index.html` file in your browser to view the detailed coverage report.

---

### Maven, Docker and Kubernetes Running Process


### Maven Run
To build and run the application with `Maven`, please follow the directions shown below;

```sh
$ cd pdfcompare
$ mvn clean install
$ mvn spring-boot:run
```

---

### Docker Run
The application can be built and run by the `Docker` engine. The `Dockerfile` has multistage build, so you do not need to build and run separately.

Please follow directions shown below in order to build and run the application with Docker Compose file;

```sh
$ cd pdfcompare
$ docker-compose up -d
```

If you change anything in the project and run it on Docker, you can also use this command shown below

```sh
$ cd pdfcompare
$ docker-compose up --build
```

To monitor the application, you can use the following tools:

- **Prometheus**:  
  Open in your browser at [http://localhost:9090](http://localhost:9090)  
  Prometheus collects and stores application metrics.

  Alert is alrealdy defined in Alert tab


- **Grafana**:  
  Open in your browser at [http://localhost:3000](http://localhost:3000)  
  Grafana provides a dashboard for visualizing the metrics.  
  **Default credentials**:
    - Username: `admin`
    - Password: `admin`


- **AlertManager**:  
  Open in your browser at [http://localhost:9093](http://localhost:9093)  

Define prometheus data source url, use this link shown below

```
http://prometheus:9090
```

Define alertManager data source url, use this link shown below

```
http://alertmanager:9093
```

---


### Kubernetes Run
To build and run the application with `Maven`, please follow the directions shown below;

- Start Minikube

```sh
$ minikube start
```

- Open Minikube Dashboard

```sh
$ minikube dashboard
```

- To deploy the application on Kubernetes, apply the Kubernetes configuration file underneath k8s folder

```sh
$ kubectl apply -f k8s
```

- To open Prometheus, click tunnel url link provided by the command shown below to reach out Prometheus

```sh
minikube service prometheus-service
```

- To open Grafana, click tunnel url link provided by the command shown below to reach out Prometheus

```sh
minikube service grafana-service
```

- To open AlertManager, click tunnel url link provided by the command shown below to reach out Prometheus

```sh
minikube service alertmanager-service
```

- Define prometheus data source url, use this link shown below

```
http://prometheus-service.default.svc.cluster.local:9090
```

- Define alertmanager data source url, use this link shown below

```
http://alertmanager-service.default.svc.cluster.local:9093
```

### Define Alert through Grafana

- Go to `localhost:9093` for Docker and Go there through `minikube service alertmanager-service` for Kubernetes
- Define `Your Gmail address` for `Contract Point` and determine if test mail is send to its email
- After define jvm micrometer dashboard in Grafana with its id 4701, click `Heap Used Panel` edit and `More -> New Alert Rules` 
- Define `Threshold` as input assigning to `A` with `IS ABOVE` as `1.8`  
- Create a new folder names for `3. Add folder and labels` and `4. Set evaluation behaviour`
- Define `Contract Points` for your defined email in `5. Configure notification`
- After reaching the threshold value, it triggers to send an alert notification to your defined mail

### Alert Manager

- Pre-configured Alert Rules: 
  - The alert is pre-defined in the `rule` file within `Prometheus`, streamlining your monitoring setup
- Threshold-based Trigger: 
  - Once any metric exceeds its designated threshold, the `alert` is automatically activated
- Instant Email Notifications: 
  - Upon triggering, `Alert Manager` sends an immediate `email notification` to your defined `email`, keeping you informed in real time


---
### Docker Image Location

```
https://hub.docker.com/repository/docker/noyandocker/pdfcompare/general
```

### Screenshots

<details>
<summary>Click here to show the screenshots of project</summary>
    <p> Figure 1 </p>
    <img src ="screenshots/postman_result.PNG">
    <p> Figure 2 </p>
    <img src ="screenshots/openapi_swagger.PNG">
    <p> Figure 3 </p>
    <img src ="screenshots/jacoco_test_coverage_report.PNG">
    <p> Figure 4 </p>
    <img src ="screenshots/docker_prometheus.PNG">
    <p> Figure 5 </p>
    <img src ="screenshots/docker_grafana_1.PNG">
    <p> Figure 6 </p>
    <img src ="screenshots/docker_grafana_2.PNG">
    <p> Figure 7 </p>
    <img src ="screenshots/docker_grafana_3.PNG">
    <p> Figure 8 </p>
    <img src ="screenshots/docker_grafana_4.PNG">
    <p> Figure 9 </p>
    <img src ="screenshots/docker_grafana_5.PNG">
    <p> Figure 10 </p>
    <img src ="screenshots/docker_grafana_6.PNG">
    <p> Figure 11 </p>
    <img src ="screenshots/docker_alert_manager_1.PNG">
    <p> Figure 12 </p>
    <img src ="screenshots/docker_grafana_alert_1.PNG">
    <p> Figure 13 </p>
    <img src ="screenshots/docker_grafana_alert_2.PNG">
    <p> Figure 14 </p>
    <img src ="screenshots/docker_grafana_alert_3.PNG">
    <p> Figure 15 </p>
    <img src ="screenshots/docker_grafana_alert_4.PNG">
    <p> Figure 16 </p>
    <img src ="screenshots/docker_grafana_alert_5.PNG">
    <p> Figure 17 </p>
    <img src ="screenshots/docker_grafana_alert_6.PNG">
    <p> Figure 18 </p>
    <img src ="screenshots/docker_grafana_alert_7.PNG">
    <p> Figure 19 </p>
    <img src ="screenshots/docker_grafana_alert_8.PNG">
    <p> Figure 20 </p>
    <img src ="screenshots/docker_grafana_alert_9.PNG">
    <p> Figure 21 </p>
    <img src ="screenshots/docker_grafana_alert_10.PNG">
    <p> Figure 22 </p>
    <img src ="screenshots/docker_grafana_alert_11.PNG">
    <p> Figure 23 </p>
    <img src ="screenshots/docker_grafana_alert_12.PNG">
    <p> Figure 24 </p>
    <img src ="screenshots/docker_grafana_alertmanager_1.PNG">
    <p> Figure 25 </p>
    <img src ="screenshots/docker_grafana_alertmanager_2.PNG">
    <p> Figure 26 </p>
    <img src ="screenshots/docker_grafana_alertmanager_3.PNG">
    <p> Figure 27 </p>
    <img src ="screenshots/docker_grafana_alertmanager_4.PNG">
    <p> Figure 28 </p>
    <img src ="screenshots/docker_grafana_alertmanager_5.PNG">
    <p> Figure 29 </p>
    <img src ="screenshots/kubernetes_prometheus_1.PNG">
    <p> Figure 30 </p>
    <img src ="screenshots/kubernetes_grafana_1.PNG">
    <p> Figure 31 </p>
    <img src ="screenshots/kubernetes_grafana_2.PNG">
    <p> Figure 32 </p>
    <img src ="screenshots/kubernetes_grafana_3.PNG">
    <p> Figure 33 </p>
    <img src ="screenshots/kubernetes_grafana_4.PNG">
    <p> Figure 34 </p>
    <img src ="screenshots/kubernetes_grafana_5.PNG">
    <p> Figure 35 </p>
    <img src ="screenshots/kubernetes_grafana_6.PNG">
    <p> Figure 36 </p>
    <img src ="screenshots/kubernetes_grafana_7.PNG">
    <p> Figure 37 </p>
    <img src ="screenshots/kubernetes_grafana_8.PNG">
    <p> Figure 38 </p>
    <img src ="screenshots/kubernetes_grafana_9.PNG">
    <p> Figure 39 </p>
    <img src ="screenshots/kubernetes_grafana_alert_1.PNG">
    <p> Figure 40 </p>
    <img src ="screenshots/kubernetes_grafana_alert_manager_1.PNG">
</details>


### Contributors

- [Sercan Noyan Germiyanoğlu](https://github.com/Rapter1990)