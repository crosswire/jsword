tx-push:
	tx push -s -r andbible.biblenames

	# cp src/main/resources/BibleNames.properties src/main/resources/BibleNames_en_XX.properties
	# rm src/main/resources/BibleNames_en_XX.properties

tx-pull:
	tx pull --force --all
	# Download language corrections to english (en_GB in transifex, mapped to en via transifex config)
	# tx pull -l en_GB --force --minimum-perc 0 -r andbible.biblenames
	# move en_GB to BibleNames.properties
	mv src/main/resources/BibleNames_en_XX.properties src/main/resources/BibleNames.properties

