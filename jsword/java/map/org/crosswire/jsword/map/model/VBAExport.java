
package org.crosswire.jsword.map.model;

import java.io.PrintWriter;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.common.util.LogicError;

/**
 * VBAExport takes a Map and exports the data to a Word VBA file.
 * This was developed from a VB macro that looked something like this:
 * <pre>
 * Attribute VB_Name = "NumberLine"
 * 
 * Option Explicit
 * 
 * Dim shape As FreeformBuilder
 * 
 * Private Const box_width As Single = 20#
 * Private Const box_height As Single = 20#
 * 
 * 
 * '-------------------------------------------------------------------------------
 * 
 * Public Sub RouteMain()
 * 
 *   RouteStart 100#, 100#, "1"
 * 
 *   RouteContinue 110#, 110#, "2"
 *   RouteContinue 120#, 120#, "3"
 *   RouteContinue 130#, 120#, "4"
 *   RouteContinue 130#, 130#, "5"
 *   RouteContinue 100#, 130#, "6"
 *   RouteContinue 90#, 100#, "7"
 * 
 *   RouteEnd
 * 
 * End Sub
 * 
 * 
 * '-------------------------------------------------------------------------------
 * 
 * Private Sub RouteStart(x As Single, y As Single, display As String)
 * 
 *   Set shape = ActiveDocument.Shapes.BuildFreeform(msoEditingAuto, x, y)
 *   RouteAddText x, y, display
 * 
 * End Sub
 * 
 * 
 * '-------------------------------------------------------------------------------
 * 
 * Private Sub RouteContinue(x As Single, y As Single, display As String)
 * 
 *   shape.AddNodes msoSegmentLine, msoEditingAuto, x, y
 *   RouteAddText x, y, display
 * 
 * End Sub
 * 
 * 
 * '-------------------------------------------------------------------------------
 * 
 * Private Sub RouteEnd()
 * 
 *   Dim route As ShapeRange
 * 
 *   shape.ConvertToShape.Select
 *   Set route = Selection.ShapeRange
 * 
 *   route.Fill.Visible = msoFalse
 *   route.Fill.Transparency = 0#
 *   route.Line.Weight = 0.75
 *   route.Line.DashStyle = msoLineSolid
 *   route.Line.Style = msoLineSingle
 *   route.Line.Transparency = 0#
 *   route.Line.Visible = msoTrue
 *   route.Line.ForeColor.RGB = RGB(255, 0, 0)
 *   route.Line.BackColor.RGB = RGB(255, 255, 255)
 *   route.Line.BeginArrowheadLength = msoArrowheadLengthMedium
 *   route.Line.BeginArrowheadWidth = msoArrowheadWidthMedium
 *   route.Line.BeginArrowheadStyle = msoArrowheadNone
 *   route.Line.EndArrowheadLength = msoArrowheadLengthMedium
 *   route.Line.EndArrowheadWidth = msoArrowheadWidthMedium
 *   route.Line.EndArrowheadStyle = msoArrowheadNone
 * 
 *   'route.LockAspectRatio = msoFalse
 *   'route.Height = 115.35
 *   'route.Width = 333.05
 *   'route.Rotation = 0#
 *   'route.RelativeHorizontalPosition = wdRelativeHorizontalPositionColumn
 *   'route.RelativeVerticalPosition = wdRelativeVerticalPositionParagraph
 * 
 *   'route.LockAnchor = False
 *   'route.WrapFormat.AllowOverlap = True
 *   'route.WrapFormat.Side = wdWrapBoth
 *   'route.WrapFormat.DistanceTop = CentimetersToPoints(0)
 *   'route.WrapFormat.DistanceBottom = CentimetersToPoints(0)
 *   'route.WrapFormat.DistanceLeft = CentimetersToPoints(0.32)
 *   'route.WrapFormat.DistanceRight = CentimetersToPoints(0.32)
 *   'route.WrapFormat.Type = 3
 * 
 *   route.ZOrder msoSendToBack
 *   route.IncrementLeft box_width / 2
 *   route.IncrementTop box_height / 2
 * 
 * End Sub
 * 
 * 
 * '-------------------------------------------------------------------------------
 * 
 * Private Sub RouteAddText(x As Single, y As Single, display As String)
 * 
 *   Dim box As shape
 * 
 *   Set box = ActiveDocument.Shapes.AddTextbox(msoTextOrientationHorizontal, x, y, box_width, box_width)
 * 
 *   box.TextFrame.TextRange.Text = display
 *   box.Fill.Visible = msoFalse
 *   box.Fill.Transparency = 0#
 *   box.Line.Weight = 0.75
 *   box.Line.DashStyle = msoLineSolid
 *   box.Line.Style = msoLineSingle
 *   box.Line.Transparency = 0#
 *   box.Line.Visible = msoFalse
 * 
 *   'box.ScaleWidth 0.38, msoFalse, msoScaleFromTopLeft
 *   'box.ScaleHeight 0.25, msoFalse, msoScaleFromTopLeft
 * 
 *   'box.LockAspectRatio = msoFalse
 *   'box.RelativeHorizontalPosition = wdRelativeHorizontalPositionColumn
 *   'box.RelativeVerticalPosition = wdRelativeVerticalPositionParagraph
 *   'box.LockAnchor = False
 * 
 *   'box.WrapFormat.AllowOverlap = True
 *   'box.WrapFormat.Side = wdWrapBoth
 *   'box.WrapFormat.DistanceTop = CentimetersToPoints(0)
 *   'box.WrapFormat.DistanceBottom = CentimetersToPoints(0)
 *   'box.WrapFormat.DistanceLeft = CentimetersToPoints(0.32)
 *   'box.WrapFormat.DistanceRight = CentimetersToPoints(0.32)
 *   'box.WrapFormat.Type = 3
 * 
 *   'box.ZOrder 5
 * 
 *   'box.TextFrame.MarginLeft = 7.09
 *   'box.TextFrame.MarginRight = 7.09
 *   'box.TextFrame.MarginTop = 3.69
 *   'box.TextFrame.MarginBottom = 3.69
 * 
 *   'box.IncrementLeft 47.7
 *   'box.IncrementTop 47.7
 *   'box.Left = CentimetersToPoints(1)
 *   'box.Top = CentimetersToPoints(0.23)
 *   'box.Height = 18.15
 *   'box.Width = 26.95
 * 
 * End Sub
 * 
 * '-------------------------------------------------------------------------------
 * </pre>
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
public class VBAExport
{
    /**
    * Basic constructor
    */
    public VBAExport()
    {
    }

    /**
    * How much do we magnify the original coords (0.0 - 1.0) by to get
    * the desired output range. The default is 500 which will fit on an
    * A4 sheet of paper
    * @param x_mag The new x magnification factor
    */
    public void setXMagnification(int x_mag)
    {
        this.x_mag = x_mag;
    }

    /**
    * How much do we magnify the original coords (0.0 - 1.0) by to get
    * the desired output range. The default is 500 which will fit on an
    * A4 sheet of paper
    * @param y_mag The new x magnification factor
    */
    public void setYMagnification(int y_mag)
    {
        this.y_mag = y_mag;
    }

    /**
    * Export the given Map file to the given stream
    * @param map The data to export
    * @param out The place to write the VBA file
    */
    public void export(Map map, PrintWriter out)
    {
        exportPreamble(out);

        out.println("");
        out.println("Public Sub RouteMain()");

        int x;
        int y;

        try
        {
            int bie = BibleInfo.booksInBible();
            for (int b=1; b<=bie; b++)
            {
                x = (int) (map.getPositionDimension(b, 1, 0) * x_mag);
                y = (int) (map.getPositionDimension(b, 1, 1) * y_mag);

                out.println("  RouteStart "+x+"#, "+y+"#, \""+BibleInfo.getShortBookName(b)+"\"");

                int cib = BibleInfo.chaptersInBook(b);
                for (int c=2; c<=cib; c++)
                {
                    x = (int) (map.getPositionDimension(b, c, 0) * x_mag);
                    y = (int) (map.getPositionDimension(b, c, 1) * y_mag);

                    out.println("  RouteContinue "+x+"#, "+y+"#, \""+c+"\"");
                }

                out.println("  RouteEnd");
                out.println("");
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }

        out.println("End Sub");

        exportSubRouteStart(out);
        exportSubRouteContinue(out);
        exportSubRouteEnd(out);
        exportSubRouteAddText(out);
    }

    /**
    * The common bits at the start of every file
    * @param out The place to write the VBA file
    */
    protected static void exportPreamble(PrintWriter out)
    {
        out.println("Attribute VB_Name = \"NumberLine\"");
        out.println("");
        out.println("Option Explicit");
        out.println("Dim shape As FreeformBuilder");
        out.println("Private Const box_width As Single = 20#");
        out.println("Private Const box_height As Single = 20#");
    }

    /**
    * The common bits at the start of every file
    * @param out The place to write the VBA file
    */
    protected static void exportSubRouteStart(PrintWriter out)
    {
        out.println("");
        out.println("Private Sub RouteStart(x As Single, y As Single, display As String)");
        out.println("  Set shape = ActiveDocument.Shapes.BuildFreeform(msoEditingAuto, x, y)");
        out.println("  RouteAddText x, y, display");
        out.println("End Sub");
    }

    /**
    * The common bits at the start of every file
    * @param out The place to write the VBA file
    */
    protected static void exportSubRouteContinue(PrintWriter out)
    {
        out.println("");
        out.println("Private Sub RouteContinue(x As Single, y As Single, display As String)");
        out.println("  shape.AddNodes msoSegmentLine, msoEditingAuto, x, y");
        out.println("  RouteAddText x, y, display");
        out.println("End Sub");
    }

    /**
    * The common bits at the start of every file
    * @param out The place to write the VBA file
    */
    protected static void exportSubRouteEnd(PrintWriter out)
    {
        out.println("");
        out.println("Private Sub RouteEnd()");
        out.println("  Dim route As ShapeRange");
        out.println("  shape.ConvertToShape.Select");
        out.println("  Set route = Selection.ShapeRange");
        out.println("  route.Fill.Visible = msoFalse");
        out.println("  route.Fill.Transparency = 0#");
        out.println("  route.Line.Weight = 0.75");
        out.println("  route.Line.DashStyle = msoLineSolid");
        out.println("  route.Line.Style = msoLineSingle");
        out.println("  route.Line.Transparency = 0#");
        out.println("  route.Line.Visible = msoTrue");
        out.println("  route.Line.ForeColor.RGB = RGB(255, 0, 0)");
        out.println("  route.Line.BackColor.RGB = RGB(255, 255, 255)");
        out.println("  route.Line.BeginArrowheadLength = msoArrowheadLengthMedium");
        out.println("  route.Line.BeginArrowheadWidth = msoArrowheadWidthMedium");
        out.println("  route.Line.BeginArrowheadStyle = msoArrowheadNone");
        out.println("  route.Line.EndArrowheadLength = msoArrowheadLengthMedium");
        out.println("  route.Line.EndArrowheadWidth = msoArrowheadWidthMedium");
        out.println("  route.Line.EndArrowheadStyle = msoArrowheadNone");
        out.println("  route.ZOrder msoSendToBack");
        out.println("  route.IncrementLeft box_width / 2");
        out.println("  route.IncrementTop box_height / 2");
        out.println("End Sub");
    }

    /**
    * The common bits at the start of every file
    * @param out The place to write the VBA file
    */
    protected static void exportSubRouteAddText(PrintWriter out)
    {
        out.println("");
        out.println("Private Sub RouteAddText(x As Single, y As Single, display As String)");
        out.println("  Dim box As shape");
        out.println("  Set box = ActiveDocument.Shapes.AddTextbox(msoTextOrientationHorizontal, x, y, box_width, box_width)");
        out.println("  box.TextFrame.TextRange.Text = display");
        out.println("  box.Fill.Visible = msoFalse");
        out.println("  box.Fill.Transparency = 0#");
        out.println("  box.Line.Weight = 0.75");
        out.println("  box.Line.DashStyle = msoLineSolid");
        out.println("  box.Line.Style = msoLineSingle");
        out.println("  box.Line.Transparency = 0#");
        out.println("  box.Line.Visible = msoFalse");
        out.println("End Sub");
    }

    /** The x magnification factor */
    private int x_mag = 500;

    /** The y magnification factor */
    private int y_mag = 500;
}
