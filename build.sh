#!/bin/sh
HOST=agvozdev@web.sourceforge.net

DEST_UPDATE_SITE=$HOST:/home/project-web/versiontree/htdocs/updates
DEST_ARCHIVE="$HOST:/home/frs/project/versiontree/versiontree\ (Eclipse\ 3.7)/net.sf.versiontree.update-site_latest-build.zip"

ARTEFACT=net.sf.versiontree.archive/net.sf.versiontree.update-site.*.zip
UPDATE_SITE=net.sf.versiontree.archive/update-site

rm -rf net.sf.versiontree.archive
mvn -P production clean install

ls -l $ARTEFACT
if [ -f $ARTEFACT ];then
  rsync -av $ARTEFACT "$DEST_ARCHIVE"
  rsync -avr --delete $UPDATE_SITE/* $DEST_UPDATE_SITE
fi
