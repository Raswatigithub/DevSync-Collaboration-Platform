const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });
const axios = require('axios');
const simpleGit = require('simple-git');
const fs = require('fs');

const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const REPO_NAME = process.argv[2];
const DESCRIPTION = process.argv[3] || 'Automated Node.js Setup for DevSync';
const WORKSPACE = path.join(__dirname, '..'); // Points to parent directory (devsync project root)
const git = simpleGit(WORKSPACE);

if (!GITHUB_TOKEN || !REPO_NAME) {
    console.error('❌ Usage: GITHUB_TOKEN=your_token node githubSetup.js <repo-name>');
    process.exit(1);
}

async function runNodeSetup() {
    try {
        // 1. Fetch user to construct URL
        const userRes = await axios.get('https://api.github.com/user', {
            headers: { Authorization: `token ${GITHUB_TOKEN}` }
        });
        const githubUser = userRes.data.login;

        // 2. Create remote repo
        console.log('📦 Creating repository on GitHub...');
        try {
            await axios.post('https://api.github.com/user/repos',
                { name: REPO_NAME, description: DESCRIPTION, private: false },
                { headers: { Authorization: `token ${GITHUB_TOKEN}` } }
            );
            console.log('✅ GitHub repo created.');
        } catch (err) {
            if (err.response && err.response.status === 422) {
                console.log('⚠️ Repository already exists. Proceeding to push...');
            } else {
                throw err;
            }
        }

        // 3. Local Git operations
        console.log('🌱 Initializing and committing...');
        if (!fs.existsSync(path.join(WORKSPACE, '.git'))) {
            await git.init();
        }

        // Create simple ignore if not exists
        if (!fs.existsSync(path.join(WORKSPACE, '.gitignore'))) {
            fs.writeFileSync(path.join(WORKSPACE, '.gitignore'), 'node_modules/\n.env\ndist/\ntarget/\n*.log\n');
        }

        await git.add('./*');
        try {
            await git.commit('chore: automated commit via Node.js');
        } catch (err) {
            // ignore if nothing to commit
        }

        // 4. Push
        console.log('☁️ Pushing to GitHub...');
        const remoteUrl = `https://${GITHUB_TOKEN}@github.com/${githubUser}/${REPO_NAME}.git`;

        // Check for existing remote
        const remotes = await git.getRemotes();
        if (remotes.some(r => r.name === 'origin')) {
            await git.remote(['set-url', 'origin', remoteUrl]);
        } else {
            await git.addRemote('origin', remoteUrl);
        }

        await git.branch(['-M', 'main']);
        await git.push(['-u', 'origin', 'main']);
        console.log(`🎉 Success! https://github.com/${githubUser}/${REPO_NAME}`);

    } catch (error) {
        console.error('❌ Automation Error:', error.response ? error.response.data : error.message);
        process.exit(1);
    }
}

runNodeSetup();
