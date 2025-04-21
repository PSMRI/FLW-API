# Flw-API
Flw-API is a microservice which is used for the creation and management of beneficiaries related data

## Filing Issues

If you encounter any issues, bugs, or have feature requests, please file them in the [main AMRIT repository](https://github.com/PSMRI/AMRIT/issues). Centralizing all feedback helps us streamline improvements and address concerns efficiently.  

## Join Our Community

We’d love to have you join our community discussions and get real-time support!  
Join our [Discord server](https://discord.gg/FVQWsf5ENS) to connect with contributors, ask questions, and stay updated.  

## Setting Up Commit Hooks

This project uses Git hooks to enforce consistent code quality and commit message standards. Even though this is a Java project, the hooks are powered by Node.js. Follow these steps to set up the hooks locally:

### Prerequisites
- Node.js (v14 or later)
- npm (comes with Node.js)

### Setup Steps

1. **Install Node.js and npm**
   - Download and install from [nodejs.org](https://nodejs.org/)
   - Verify installation with:
     ```
     node --version
     npm --version
     ```
2. **Install dependencies**
   - From the project root directory, run:
     ```
     npm ci
     ```
   - This will install all required dependencies including Husky and commitlint
3. **Verify hooks installation**
   - The hooks should be automatically installed by Husky
   - You can verify by checking if the `.husky` directory contains executable hooks

### Commit Message Convention
This project follows a specific commit message format:
- Format: `type(scope): subject`
- Example: `feat(login): add remember me functionality`

Types include:
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code changes that neither fix bugs nor add features
- `perf`: Performance improvements
- `test`: Adding or fixing tests
- `build`: Changes to build process or tools
- `ci`: Changes to CI configuration
- `chore`: Other changes (e.g., maintenance tasks, dependencies)

Your commit messages will be automatically validated when you commit, ensuring project consistency.

### Using Commitizen

For an interactive commit message helper, you can use Commitizen:
```
npm run commit
```
This will guide you through creating a properly formatted commit message.
