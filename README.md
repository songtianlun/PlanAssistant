
![ic_launcher_round.png](https://i.loli.net/2019/05/26/5cea9bdf9020a96716.png)


# Plan-Assistant

[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)  ![CRAN/METACRAN](https://img.shields.io/cran/l/devtools.svg?color=green&label=LIcanse&logo=green&logoColor=red)  [![](https://img.shields.io/badge/作者博客-frytea.com-green.svg)](https://frytea.com)  

This software is developed for people who like to record their lives.


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


## To-do

- [x] Android X
- [x] start ui
- [x] chart
- [x] setting ui
- [x] about ui
- [x] BaiDu Statistic
- [x] login
- [x] sign out
- [x] change password
- [x] change info
- [x] personal info ui
- [x] tencent location
- [x] data to leancloud background
- [x] load data to mapbox
- [x] location setting
- [x] track map
- [x] quare track to show hot map
- [x] show liveline
- [x] manager liveline data
- [x] show personal map
- [ ] unverified account processing
- [ ] analyse track
- [ ] ui
- [ ] sql
- [ ] location
- [ ] map
- [ ] record

## some algorithm

### track analyse
find location where user stop.

No.1: Calculate **the number of points** in the space-time range.

No.2: Analyse **marker points** by time and space range points and spatial extent.

### describe