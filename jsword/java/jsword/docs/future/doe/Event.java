
package docs.future.doe;

import java.util.Date;

import org.crosswire.jsword.passage.Passage;

/**
 * @stereotype moment-interval 
 */
public class Event
{
    public Date getTime(){
            return time;
        }

    public void setTime(Date time){
            this.time = time;
        }

    public Passage getPassage(){
            return ref;
        }

    public void setPassage(Passage ref){
            this.ref = ref;
        }

    public String getHeadline(){
            return headline;
        }

    private Date time;

    private String headline;

    private Passage ref;
}
