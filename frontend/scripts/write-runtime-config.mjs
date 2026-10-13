import { mkdir, writeFile } from 'node:fs/promises';
const publicDir = new URL('../public/', import.meta.url);
await mkdir(publicDir, { recursive: true });
const apiBase = (process.env.NG_APP_API_BASE || '/api').replace(/\/$/, '');
await writeFile(new URL('runtime-config.js', publicDir), `globalThis.salaryApiBase = ${JSON.stringify(apiBase)};\n`);
