# Production frontend API URL

Date: 2026-10-15

## Prompt used

Set the production API URL to https://salary-management-1vd3.onrender.com/api. Check Angular file replacements, centralize HTTP access, run the production build, commit, and push to master.

## Findings and changes

There were no environment file replacements. A generated runtime script supplied `/api`, and Netlify proxied that path to an outdated Render service name. Add explicit development/production environments and a production replacement. The core API service reads `environment.apiUrl`; all feature services already use that core service for JSON, file uploads, and downloads. Remove the runtime override and outdated Netlify proxy so neither can override the user's URL. Local Compose explicitly builds the development configuration to retain its local API proxy. Update architecture and setup notes.

## Verification

The Docker frontend build ran `npm run build -- --configuration=production` successfully. The initial bundle is 499.92 kB, below the configured budget. The resulting image contains the requested Render URL in its JavaScript and has no runtime-config override. Application source was searched for direct HttpClient use, fetch/XMLHttpRequest, and hardcoded URL alternatives. Only the core service owns HTTP calls; only the production environment defines the deployed API URL. No unit or browser suites were run for this build-only request. Public CORS and hosted availability remain deployment verification steps.
