import { spawnSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const cli = path.join(root, 'node_modules', 'playwright', 'cli.js');
const suite = process.argv[2] ?? 'all';
const args = ['test'];
if (suite !== 'all') args.push('--grep', `@${suite}`);
const testRun = spawnSync(process.execPath, [cli, ...args], { cwd: root, stdio: 'inherit', env: process.env });
spawnSync(process.execPath, [path.join(root, 'scripts', 'generate-report.mjs')], { cwd: root, stdio: 'inherit', env: process.env });
process.exit(testRun.status ?? 1);
