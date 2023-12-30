
# Project 2: Voting-System
Team 03:
- Al-Ekram
- Victor Pepel (Manager)
- Thinh Pham
- Michel Robert
- Nicholas Sullivan
- Andrew Zhuang

The Voting-System is a comprehensive electronic voting platform designed to facilitate secure and efficient elections. It integrates advanced technologies to ensure the integrity, confidentiality, and accessibility of the voting process.

## Usage:
The system offers various functionalities for both voters and administrators, ensuring a smooth and secure voting experience.

### For Voters:
- **Casting Votes**: Voters can use the VoterGUI interface to cast their votes.
  - Navigate to the VoterGUI on the voting machine.
  - Select your preferred candidates using the intuitive interface.
  - Confirm and submit your vote.

### For Administrators:
- **AdminGUI (`AdminGUI.java`)**: This interface is used by election officials to manage the voting process.
  - Key Methods: `createLoginScene`, `checkCredentials`, `createMainScene`.

- **Authentication Module (`AuthenticationModule.java`)**: Ensures the security of the voting process.
  - Key Methods: `readLogin`, `resetPassword`, `verifyLogin`.

- **Access Log (`AccessLog.java`)**: Keeps a record of all system interactions for auditing purposes.
  - Key Methods: `updateLog`, `getLog`.

- **Poll Management (`PollController.java`)**: Administers the opening, closing, and monitoring of polls.
  - Key Methods: `resetPoll`, `startPoll`, `countVotes`.

### Additional Features:
- **Ballot Management**: The system processes and stores ballot information efficiently, ensuring accurate vote counting.
- **Real-time Updates**: Election results are updated in real-time, providing immediate insights into the voting trends.
- **Security Measures**: Incorporates robust security protocols to safeguard against unauthorized access and tampering.

## System Features:
- **Touchscreen Interface**: Both VoterGUI and AdminGUI are accessible via touchscreen displays, offering an intuitive user experience.
- **Mobile Integration**: The system can be monitored and controlled remotely using specialized mobile applications.
- **Data Encryption**: All data transmitted within the system is securely encrypted.
- **Backup Systems**: In case of power failures or technical issues, backup systems ensure the continuity of the voting process.

## Software Architecture:
The Voting-System is architecturally designed to be modular and scalable. It consists of several interconnected components, each responsible for specific functionalities within the voting process. The architecture ensures seamless communication between the VoterGUI, AdminGUI, and backend systems.

