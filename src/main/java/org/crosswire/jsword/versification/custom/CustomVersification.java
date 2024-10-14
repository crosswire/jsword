package org.crosswire.jsword.versification.custom;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.sword.SwordConstants;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomVersification {

    public void loadFromJSON(final File jsonFile) throws NoSuchKeyException, BookException, IOException {

        //read json file data to String
        File f;

        byte[] jsonData = Files.readAllBytes(Paths.get(jsonFile.getPath()));
        loadFromJSON(jsonData);
    }

    public void loadFromJSON(final byte[] jsonData ) throws NoSuchKeyException, BookException, IOException {

        //create ObjectMapper instance and allow comments
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        //convert json string to V11nTmp object
        V11nTmp v11n = objectMapper.readValue(jsonData, V11nTmp.class);

        SystemCustomVersification.V11N_NAME = v11n.v11nName;

        int vmIndex = 0;
        SystemCustomVersification.BOOKS_OT = new BibleBook[v11n.otbooks.length - 1];
        SystemCustomVersification.LAST_VERSE_OT = new int[v11n.otbooks.length - 1][];
        for(int i = 0; i < v11n.otbooks.length - 1; i++){
            BibleBook bb = BibleBook.fromOSIS(v11n.otbooks[i].osis);
            if(bb == null)
            {
                Reporter.informUser(this, new BookException(JSMsg.gettext("Invalid OSIS name: {0}", v11n.otbooks[i].osis)));
                return;
            }
            SystemCustomVersification.BOOKS_OT[i] = bb;
            //SystemCustomVersification.BOOKS_OT[i] = BibleBook.fromOSIS(v11n.otbooks[i].osis);
            SystemCustomVersification.LAST_VERSE_OT[i] = new int[v11n.otbooks[i].chapmax];
            System.arraycopy( v11n.vm, vmIndex, SystemCustomVersification.LAST_VERSE_OT[i], 0, v11n.otbooks[i].chapmax);
            vmIndex += v11n.otbooks[i].chapmax;
        }

        SystemCustomVersification.BOOKS_NT = new BibleBook[v11n.ntbooks.length - 1];
        SystemCustomVersification.LAST_VERSE_NT = new int[v11n.ntbooks.length - 1][];
        for(int i = 0; i < v11n.ntbooks.length - 1; i++){
            SystemCustomVersification.BOOKS_NT[i] = BibleBook.fromOSIS(v11n.ntbooks[i].osis);
            SystemCustomVersification.LAST_VERSE_NT[i] = new int[v11n.ntbooks[i].chapmax];
            System.arraycopy( v11n.vm, vmIndex, SystemCustomVersification.LAST_VERSE_NT[i], 0, v11n.ntbooks[i].chapmax);
            vmIndex += v11n.ntbooks[i].chapmax;
        }

        // SM removed check for existing versification.
        // If the custom versification name is the same as an existing one, it will overwrite it
        // TODO: clean up custom properties files under JSword folder.
        Versifications.instance().register(new SystemCustomVersification());
        try {
            URI[] dirs = CWProject.instance().getProjectResourceDirs();
            final File parent = new File(dirs[0]);
            File mapFile = new File(parent, SystemCustomVersification.V11N_NAME + ".properties");
            if (mapFile.exists()) mapFile.delete();
            if(v11n.jsword_mappings.length > 0) {
                mapFile.createNewFile();
                FileWriter myWriter = new FileWriter(mapFile.getPath());
                for (int i = 0; i < v11n.jsword_mappings.length; i++) {
                    myWriter.write(v11n.jsword_mappings[i]);
                    myWriter.write("\n");
                }
                myWriter.close();
            }

        } catch (IOException ex) {
            log.error("Failed to process custom versification file", ex);
        }

    }

    public void RemoveCustomVersification(String name) {

        try {
            URI stepFolder = CWProject.instance().getWriteableFrontendProjectDir();

            File jsonFile = null;
            if (stepFolder != null) {
                final File parent = new File(stepFolder);
                File versificationFolder = new File(parent, SwordConstants.DIR_VERSIFICATION);
                jsonFile = new File(versificationFolder, name + ".json");
            }

            // if there is a json file
            if (jsonFile != null && jsonFile.exists()) {
                // delete it
                jsonFile.delete();

                // and the properties file
                URI[] dirs = CWProject.instance().getProjectResourceDirs();
                final File parent = new File(dirs[0]);
                File mapFile = new File(parent, name + ".properties");
                mapFile.delete();

                // finally de-register the custom versification
                Versifications.instance().deregiter(name);
            }
        } catch (Exception ex) {
            log.error("Failed to process custom versification file", ex);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(CustomVersification.class);
}
