#!/bin/sh
HOST=agvozdev@web.sourceforge.net

if [ "$1" = "release" ];then
  ## Blessed update site for releases is $HOST:/home/project-web/versiontree/htdocs/updates
  DEST_UPDATE_SITE=$HOST:/home/project-web/versiontree/htdocs/updates
  DEST_ARCHIVE="$HOST:/home/frs/project/versiontree/versiontree\ (Eclipse\ 3.7)/net.sf.versiontree.update-site_1.7.X.zip"
else
  DEST_UPDATE_SITE=$HOST:/home/project-web/versiontree/htdocs/update-site-unstable
  DEST_ARCHIVE="$HOST:/home/frs/project/versiontree/versiontree\ (Eclipse\ 3.7)/net.sf.versiontree.update-site-unstable.zip"
fi

ARTEFACT=net.sf.versiontree.archive/net.sf.versiontree.update-site.*.zip
UPDATE_SITE=net.sf.versiontree.archive/update-site

rm -rf net.sf.versiontree.archive
mvn -P production clean install

chmod -R 644 $ARTEFACT $UPDATE_SITE
chmod a+x $UPDATE_SITE $UPDATE_SITE/features $UPDATE_SITE/plugins

ls -l $ARTEFACT
if [ -f $ARTEFACT ];then
  rsync -av $ARTEFACT "$DEST_ARCHIVE"
  rsync -avr --delete $UPDATE_SITE/* $DEST_UPDATE_SITE
fi
