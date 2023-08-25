# fatboySIM

## Instructions
Perform all of these in the same directory as this readme file.
1. Download https://github.com/martinpaljak/ant-javacard/releases/download/v23.08.07/ant-javacard.jar as `./lib/ant-javacard_v23-08-07.jar`.
2. Download this directory https://github.com/martinpaljak/oracle_javacard_sdks/tree/master/jc221_kit as `./lib/java-card-kit_v2-2-1/`.
3. Unpack https://dlcdn.apache.org//ant/binaries/apache-ant-1.10.14-bin.tar.gz as `./lib/apache-ant_v1-10-13/`.
4. Unpack https://builds.openlogic.com/downloadJDK/openlogic-openjdk/8u382-b05/openlogic-openjdk-8u382-b05-linux-x64.tar.gz as `./lib/openlogic-openjdk_v8u382-b05-linux-x64/`.
5. Unpack ftp://ftp.3gpp.org/Specs/archive/43_series/43.019/43019-600.zip as `./lib/3gpp_43-019_600/`.
6. Unpack `./lib/3gpp_43-019_600/Annex_A_java.zip` as `./lib/3gpp_43-019_600/Annex_A_java/`.
7. Unpack `./lib/3gpp_43-019_600/Annex_B_Export_Files.zip` as `./lib/3gpp_43-019_600/Annex_B_Export_Files/`.
8. We need to create the SIM library from the files we got from 3GPP. Run `pushd . && cd ./lib/3gpp_43-019_600/Annex_A_java && jar cf ./sim.jar ./sim && popd`.
9. Run `./script/build.sh`. This will create the `./dist/FatboySIM.cap` file which we can flash.
10. Clone `https://github.com/herlesupreeth/sim-tools.git` (this is a fork of the original which supports flashing in a way that uses OTA security).
11. Input the KIc and KID of your card into the scripts at `./script/`.
12. Before flashing, I'd recommend to run the `./script/list.sh` command to make sure you have the right KIc and KID keys in the scripts. This should output a list of all installed applets. If there is no output, your keys are wrong.
13. To flash, run `./script/delete.sh` (to delete any previous versions of the applet) and `./script/install.sh`. The cap file is quite large relative what would normally be flashed, so this takes a while.

## Notes
Insert your SIM into a phone and it should work (to start the demo, open up the SIM toolkit app and click on any of the options). Some things to note are that because the `STATUS` command is sent once every 1 or 2 seconds, you'll get maybe a sub-1 FPS animation. To speed things up, I used a script that presses the screen (`./script/tap.sh`). Pressing the options sends an `ENVELOPE` command which also triggers a frame to be rendered. I wrote a mock up of the demo in a self-made [SIM card emulator](https://github.com/tomasz-lisowski/swsim) which allowed me to respond with a `91XX` status code to the `TERMINAL RESPONSE` command the phone sends back after receiving a frame (via `SET UP MENU`). The side effect of this is that it runs by itself at a high-ish frame rate, and requires no user input to work, the downside is of course that it's not a hardware SIM. In general, anything you do will not fix framerate instability, after all, we are rendering using a SIM card menu haha.

By the way, yes, everything you see is first rendered by the SIM, packaged into a `SET UP MENU` command, and sent to the phone which then renders the menu per the provided data.
You might also wonder why the recording uses a regular non-monospaced font (and hence looks horrible). This is simply because for some reason you can't easily add fonts to Android, so I decided to keep it as-is. The only thing I did was to increase the font size and make it bold so that the menu would take up the whole screen. The exact card I used in the recording was a sysmoISIM-SJA2, mainly because it's hard to get hold of SIM cards with key material.
