## Contribution
Everyone is wellcome to contribute to any platform of this project. You don't have to comment everywhere. Just comment where it is really needed and make sure your code is readable without comments and follows proper architecture.
Platform wise contribution guidelines can be found in their individual 
**README.md**.

### Version Control
The project belongs to a single git repository containing all platforms. It follows a mixture of trunk based development and git flow. Following guidelines must be followed to contribute:

#### Branching
![Git Branching Preview](preview/dtb_git_brancing.jpg)

* The **readme** branch is where all commons files are placed. 
* New platform must create a new branch from **readme**. 
* Platform's branch should be merged directly to **master**.
* Feature branch should be merged to platform's branch which was 
created from **readme**
* Common files must not be modified in the platforms' branches. They 
should be modified in the **readme** branch and merged directly to 
**master**.

#### Naming Convension
* Platform's branch name should be same as platform name. *Ex. `android`*
* Platform's folder name pattern should be, **"dtb-{platform_name}"**. *Ex. `dtb-android`*
* Feature branch's name pattern should be, **"{platform_name}-{feature_name}"**. *Ex. `android-login`*

#### Commit Message Pattern
* In general ***(except for api)*** commit message pattern should be, **"{platform_name}: {commit_message}"**. *Ex. `android: fix crash due to internet permission`*
* For **api**, commit message pattern should be, **"#{service_name}-service# {commit_message}"**. *Ex. `#auth-service# integrate facebook login`*. This will help git **post-receive** hook to restart that particular service. If your commit is not intend for restarting any service then the commit message should start with **`api: `** instead.
