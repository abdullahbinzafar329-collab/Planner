# Biyas Planner (Android)

A native, offline year-ahead reminder app. Alarms are scheduled with
Android's own AlarmManager, so they ring even if the app is closed, the
phone is locked, or there's no internet connection.

## Get an installable APK — no software to install

1. Create a free account at https://github.com if you don't have one.
2. Create a new **empty** repository (any name, e.g. `biyas-planner`).
3. On the repo page, click **Add file → Upload files**, then drag in
   every file and folder from this project (keep the folder structure —
   `app/`, `.github/`, `build.gradle`, etc.) and commit.
4. Click the **Actions** tab. A workflow called "Build APK" will run
   automatically (a few minutes). If it doesn't start, click **Run workflow**.
5. When it finishes with a green check, open the completed run and
   download the **BiyasPlanner-debug-apk** artifact — it's a zip
   containing your `.apk`.
6. Transfer that `.apk` to your phone (email it to yourself, or use
   Google Drive), tap it, and allow "install unknown apps" for that source
   when prompted. That's the actual installable app.

## Notes
- First launch will ask for notification permission and "allow exact
  alarms" — say yes to both, or reminders can't fire reliably.
- Everything is stored on-device (no account, no internet needed to work).
- If a build step fails, open the red ✕ run in the Actions tab, copy the
  error text, and send it over — it's almost always one version number
  to adjust.
