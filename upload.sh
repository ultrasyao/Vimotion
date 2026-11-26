#!/data/data/com.termux/files/usr/bin/sh
# pakai: sh upload.sh "pesan commit"
set -e
git add -A
git commit -m "${1:-update}" || true
git push
echo "Tunggu build: gh run watch  |  ambil APK: gh run download -n Vimotion-debug"
