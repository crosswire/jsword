
package org.crosswire.jsword.book.data.jaxb;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.data.BookDataListener;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.DivineName;
import org.crosswire.jsword.osis.Header;
import org.crosswire.jsword.osis.Note;
import org.crosswire.jsword.osis.OsisText;
import org.crosswire.jsword.osis.Q;
import org.crosswire.jsword.osis.Reference;
import org.crosswire.jsword.osis.Seg;
import org.crosswire.jsword.osis.Speaker;
import org.crosswire.jsword.osis.Title;
import org.crosswire.jsword.osis.TransChange;
import org.crosswire.jsword.osis.Verse;
import org.crosswire.jsword.osis.W;
import org.crosswire.jsword.osis.Work;
import org.crosswire.jsword.passage.Passage;

/**
 * A BookDataListener that adds to an OSIS bean model.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class JAXBBookDataListnener implements BookDataListener
{
    /**
     * We don't want just anyone doing this.
     */
    protected JAXBBookDataListnener()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startDocument(org.crosswire.jsword.book.BookMetaData)
     */
    public void startDocument(String osisid)
    {
        try
        {
            this.bdata = new JAXBBookData();
            bdata.osis = JAXBUtil.factory().createOsis();

            Work work = JAXBUtil.factory().createWork();
            work.setOsisWork(osisid);

            Header header = JAXBUtil.factory().createHeader();
            header.getWork().add(work);

            OsisText text = JAXBUtil.factory().createOsisText();
            text.setOsisIDWork("Bible."+osisid);
            text.setHeader(header);

            bdata.osis.setOsisText(text);

            stack.addFirst(text);
        }
        catch (JAXBException ex)
        {
            throw new LogicError();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endDocument()
     */
    public JAXBBookData endDocument()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof OsisText))
        {
            throw new LogicError();
        }

        if (!stack.isEmpty())
        {
            throw new LogicError();
        }

        return bdata;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startSection()
     */
    public void startSection(String title)
    {
        try
        {
            Div div = JAXBUtil.factory().createDiv();
            div.setDivTitle(title);

            getCurrentList(OsisText.class).add(div);

            stack.addFirst(div);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endSection()
     */
    public void endSection()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof Div))
            throw new LogicError();

        // NOTE(joe) remove this when we are more sure that there isn't a good reason for it to be here.
        // bdata.osis.getOsisText().getDiv().add((Div) top);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startVerse(org.crosswire.jsword.passage.Verse)
     */
    public void startVerse(org.crosswire.jsword.passage.Verse verse)
    {
        try
        {
            Verse everse = JAXBUtil.factory().createVerse();
            everse.setOsisID(verse.getBook()+"."+verse.getChapter()+"."+verse.getVerse());

            getCurrentList(Div.class).add(everse);

            stack.addFirst(everse);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endVerse()
     */
    public void endVerse()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof Verse))
            throw new LogicError();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#addText(java.lang.String)
     */
    public void addText(String text)
    {
        getCurrentList().add(text);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#addNote(java.lang.String, java.lang.String)
     */
    public void addNote(String marker, String addition)
    {
        try
        {
            Note note = JAXBUtil.factory().createNote();
            note.setN(marker);
            note.getContent().add(addition);

            getCurrentList().add(note);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#addDivineName(java.lang.String)
     */
    public void addDivineName(String name)
    {
        try
        {
            DivineName dname = JAXBUtil.factory().createDivineName();
            dname.getContent().add(name);

            getCurrentList().add(dname);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startQuote(java.lang.String, java.lang.String)
     */
    public void startQuote(String who, String level)
    {
        try
        {
            Q q = JAXBUtil.factory().createQ();
            q.setWho(who);
            q.setLevel(level);

            getCurrentList().add(q);

            stack.addFirst(q);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endQuote()
     */
    public void endQuote()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof Q))
            throw new LogicError();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startReference(org.crosswire.jsword.passage.Passage)
     */
    public void startReference(Passage ref)
    {
        try
        {
            Reference rref = JAXBUtil.factory().createReference();
            rref.setOsisRef(ref.getOSISName());

            getCurrentList().add(rref);

            stack.addFirst(ref);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endReference()
     */
    public void endReference()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof Reference))
            throw new LogicError();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startSegment()
     */
    public void startSegment()
    {
        try
        {
            Seg seg = JAXBUtil.factory().createSeg();

            getCurrentList().add(seg);

            stack.addFirst(seg);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endSegment()
     */
    public void endSegment()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof Seg))
            throw new LogicError();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startSpeaker(java.lang.String)
     */
    public void startSpeaker(String who)
    {
        try
        {
            Speaker speaker = JAXBUtil.factory().createSpeaker();
            speaker.setWho(who);

            getCurrentList().add(who);

            stack.addFirst(speaker);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endSpeaker()
     */
    public void endSpeaker()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof Speaker))
            throw new LogicError();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startTitle(java.lang.String)
     */
    public void startTitle()
    {
        try
        {
            Title title = JAXBUtil.factory().createTitle();

            getCurrentList().add(title);

            stack.addFirst(title);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endTitle()
     */
    public void endTitle()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof Title))
            throw new LogicError();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startTransChange(java.lang.String)
     */
    public void startTransChange(String type)
    {
        try
        {
            TransChange trans = JAXBUtil.factory().createTransChange();
            trans.setChangeType(type);

            getCurrentList().add(trans);

            stack.addFirst(trans);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endTransChange()
     */
    public void endTransChange()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof TransChange))
            throw new LogicError();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#startWord()
     */
    public void startWord()
    {
        try
        {
            W word = JAXBUtil.factory().createW();

            getCurrentList().add(word);

            stack.addFirst(word);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookDataListener#endWord()
     */
    public void endWord()
    {
        Object top = stack.removeFirst();

        // Check that we are properly tree structured
        if (!(top instanceof W))
            throw new LogicError();
    }

    /**
     * Find the current node and get a list from it.
     */
    private List getCurrentList(Class parenttype)
    {
        Object current = stack.getFirst();

        if (!parenttype.isInstance(current))
        {
            throw new LogicError("found type="+current.getClass().getName()+" but expecting="+parenttype.getName());
        }
        
        return getCurrentList();
    }

    /**
     * Find the current node and get a list from it.
     */
    protected List getCurrentList()
    {
        Object current = stack.getFirst();

        if (current instanceof Verse)
        {
            return ((Verse) current).getContent();
        }
        else if (current instanceof Q)
        {
            return ((Q) current).getContent();
        }
        else if (current instanceof Reference)
        {
            return ((Reference) current).getContent();
        }
        else if (current instanceof Seg)
        {
            return ((Seg) current).getContent();
        }
        else if (current instanceof Speaker)
        {
            return ((Speaker) current).getContent();
        }
        else if (current instanceof Title)
        {
            return ((Title) current).getContent();
        }
        else if (current instanceof TransChange)
        {
            return ((TransChange) current).getContent();
        }
        else if (current instanceof W)
        {
            return ((W) current).getContent();
        }
        else if (current instanceof Div)
        {
            return ((Div) current).getContent();
        }
        else if (current instanceof OsisText)
        {
            return ((OsisText) current).getDiv();
        }

        log.error("unknown element: "+current.getClass().getName());
        throw new LogicError();
    }

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(JAXBBookDataListnener.class);

    /**
     * The proxied OSIS bean that we add to
     */
    private JAXBBookData bdata;

    /**
     * The stack of current OSIS beans
     */
    private LinkedList stack = new LinkedList();
}
