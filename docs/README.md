
![ic_launcher_round.png](https://i.loli.net/2019/05/26/5cea9bdf9020a96716.png)


# Plan-Assistant

[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)  ![CRAN/METACRAN](https://img.shields.io/cran/l/devtools.svg?color=green&label=Licanse&logo=green&logoColor=red)  [![](https://img.shields.io/badge/作者博客-frytea.com-green.svg)](https://frytea.com)  

Planning Assistant is developed to help users achieve effective work by recording life and planning energy.


## Environment

- Dev-Platform 
    - ThinkPad-E450
    - Deepin-15.10.1
- Dev-Environment
    - Android Studio 3.4.1
    - java: 1.8.0_152_release
    - gradle: 3.4.0
    - Gradle Version: 5.1.1
    - buildToolsVersion: 29.0.0-rc3
    - compileSdkVersion: 28
    - minSdkVersion: 21
- user-Platform
	- Android api>=21

## Version

 name | version | type | link
 :--: | :--: | :--: | :--:
 PlanAssistant | v1.1.0 | release | [Download](http://frytea-data.test.upcdn.net/PlanAssistant-release-v1.1.0.apk)
 PlanAssistant | v1.0.0 | release | [Download](http://frytea-data.test.upcdn.net/PlanAssistant-release-1.0.0.apk)
 
### Log of Update(Release)

* V1.1.1
    * Add Server Select
    * Fix error of fail to use in first login.
    * Fix Main View error screen of gps.
    * Fix some bug.
* V1.1.0
    * Add Plan Counter
    * Fix some bug
* V1.0.0
    * simple framework
 
## some algorithm

### track analyse
find location where user stop.

No.1: Calculate **the number of points** in the space-time range.

No.2: Analyse **marker points** by time and space range points and spatial extent.
