# Frontline Health Workers (FLW) Mobile App API Module

### Introduction
Frontline Health Workers (FLW) Mobile App is known with brand name _SAKHI_ App. Currently this _SAKHI_ App is designed for Accredited Social Health Activist (ASHA).
ASHAs usually uses multiple registers for beneficiary registration, enumeration, data collection. This leads to challenges of manual errors, loss of data and data accuracy.
In this context Piramal Swasthya has designed and developed a Mobile application for ASHA workers to digitalize and reducing the manual work.
It is designed to reduce manual labor, enhance efficiency, and ensure timely and accurate data collection by ASHAs. All the services that are carried by ASHA in the field are available in the Mobile Application, including Services for RMNCH+A(Reproductive, Maternal, Newborn, Child, and Adolescent Health), the application enhances service delivery and monitoring for Household Enumeration, Antenatal care, Postnatal care, Immunization services, as well as Non-Communicable Diseases (NCDs) screening and Tuberculosis (TB) screening.

### Modules of FLW Application
1. Household Registration:
The application enables Household Registration with a unique HHID for households and Beneficiary IDs for family members, with the option to create Ayushman Bharat Health Account (ABHA), ensuring accurate tracking and line listing.

2. Eligible Couple List:
Maintain line listing of married couples (Married Women aged 15–49) and tracks for family planning needs and reproductive health.

3. Maternal Health:
Maternal health module comprises of pregnant women registration and line listing to provide timely and appropriate healthcare to mothers before, during, and after childbirth. That involves in capturing the information related to Antenatal visits (ANC), Deliveries, and Postnatal visits (PNC), Infant care. 

4. Child Care:
Track Infants, Children, and Adolescents for health monitoring, vaccinations, and counseling, along with HBNC and HBYC modules for comprehensive care.
5. NCD:
Line listing and Track individuals aged 30 and above (both males and females) for NCD eligibility, priority, and screening outcomes, along with CBAC (Community-Based Assessment checklist) for early detection and monitoring. 
6. Child Immunization:
Tracking and Line Listing of immunizations according to the national immunization schedule, including due and completed vaccinations, with scheduling and reminders for infants and children. 
7. HRP Assessment:
Module for Identification & Management of High-Risk Eligible couples and Pregnant Women.
Line listing and track high-risk pregnancies with reasons, referrals, and follow-ups.
8. Death Reports:
Maintains line listing of death records. And reports maintain Maternal Death Surveillance and Response (MDSR) and Child Death Review (CDR).
9. ASHA Dashboard
Quick access to all Health Indicators and key performance indicators (KPIs) and metrics related to ASHA.
10. ASHA Work Plan:
This is ASHA’s To-Do list, helps ASHA to plan her day and future days for her community visits and mobilization.
Readily available service wise due list of Beneficiaries. For example, line listing of Beneficiaries due for Pregnant Women and Children for Immunization, ANC, PMSMA, PNC, HBNC, HBYC services and VHSND session. Helps efficient tracking and mobilizing of Beneficiaries to VHSND camp.

### About FLW-API Repository
This repository comprises of all APIs related to FLW Mobile App.

Flw-API is a microservice which is used for the creation and management of beneficiaries related data

## Filing Issues

If you encounter any issues, bugs, or have feature requests, please file them in the [main AMRIT repository](https://github.com/PSMRI/AMRIT/issues). Centralizing all feedback helps us streamline improvements and address concerns efficiently.  

## Join Our Community

We’d love to have you join our community discussions and get real-time support!  
Join our [Discord server](https://discord.gg/FVQWsf5ENS) to connect with contributors, ask questions, and stay updated.  

### Building From Source
This microservice is built on Java, Spring boot framework and MySQL DB.

### Pre-requisites
* JDK 1.8
* Spring Tool Suite 3 / Eclipse(2023-03)
* Maven (if not pre-installed with the editor)
* Redis-x64-3.0.504
* MySQL Workbench 8.0