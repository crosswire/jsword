Attribute VB_Name = "NumberLine"

Option Explicit

Dim shape As FreeformBuilder

Private Const box_width As Single = 20#
Private Const box_height As Single = 20#


'-------------------------------------------------------------------------------

Public Sub RouteMain()

  RouteStart 100#, 100#, "1"

  RouteContinue 110#, 110#, "2"
  RouteContinue 120#, 120#, "3"
  RouteContinue 130#, 120#, "4"
  RouteContinue 130#, 130#, "5"
  RouteContinue 100#, 130#, "6"
  RouteContinue 90#, 100#, "7"

  RouteEnd

End Sub


'-------------------------------------------------------------------------------

Private Sub RouteStart(x As Single, y As Single, display As String)

  Set shape = ActiveDocument.Shapes.BuildFreeform(msoEditingAuto, x, y)
  RouteAddText x, y, display

End Sub


'-------------------------------------------------------------------------------

Private Sub RouteContinue(x As Single, y As Single, display As String)

  shape.AddNodes msoSegmentLine, msoEditingAuto, x, y
  RouteAddText x, y, display

End Sub


'-------------------------------------------------------------------------------

Private Sub RouteEnd()

  Dim route As ShapeRange
  
  shape.ConvertToShape.Select
  Set route = Selection.ShapeRange

  route.Fill.Visible = msoFalse
  route.Fill.Transparency = 0#
  route.Line.Weight = 0.75
  route.Line.DashStyle = msoLineSolid
  route.Line.Style = msoLineSingle
  route.Line.Transparency = 0#
  route.Line.Visible = msoTrue
  route.Line.ForeColor.RGB = RGB(255, 0, 0)
  route.Line.BackColor.RGB = RGB(255, 255, 255)
  route.Line.BeginArrowheadLength = msoArrowheadLengthMedium
  route.Line.BeginArrowheadWidth = msoArrowheadWidthMedium
  route.Line.BeginArrowheadStyle = msoArrowheadNone
  route.Line.EndArrowheadLength = msoArrowheadLengthMedium
  route.Line.EndArrowheadWidth = msoArrowheadWidthMedium
  route.Line.EndArrowheadStyle = msoArrowheadNone

  'route.LockAspectRatio = msoFalse
  'route.Height = 115.35
  'route.Width = 333.05
  'route.Rotation = 0#
  'route.RelativeHorizontalPosition = wdRelativeHorizontalPositionColumn
  'route.RelativeVerticalPosition = wdRelativeVerticalPositionParagraph

  'route.LockAnchor = False
  'route.WrapFormat.AllowOverlap = True
  'route.WrapFormat.Side = wdWrapBoth
  'route.WrapFormat.DistanceTop = CentimetersToPoints(0)
  'route.WrapFormat.DistanceBottom = CentimetersToPoints(0)
  'route.WrapFormat.DistanceLeft = CentimetersToPoints(0.32)
  'route.WrapFormat.DistanceRight = CentimetersToPoints(0.32)
  'route.WrapFormat.Type = 3

  route.ZOrder msoSendToBack
  route.IncrementLeft box_width / 2
  route.IncrementTop box_height / 2

End Sub


'-------------------------------------------------------------------------------

Private Sub RouteAddText(x As Single, y As Single, display As String)

  Dim box As shape
  
  Set box = ActiveDocument.Shapes.AddTextbox(msoTextOrientationHorizontal, x, y, box_width, box_width)

  box.TextFrame.TextRange.Text = display
  box.Fill.Visible = msoFalse
  box.Fill.Transparency = 0#
  box.Line.Weight = 0.75
  box.Line.DashStyle = msoLineSolid
  box.Line.Style = msoLineSingle
  box.Line.Transparency = 0#
  box.Line.Visible = msoFalse

  'box.ScaleWidth 0.38, msoFalse, msoScaleFromTopLeft
  'box.ScaleHeight 0.25, msoFalse, msoScaleFromTopLeft

  'box.LockAspectRatio = msoFalse
  'box.RelativeHorizontalPosition = wdRelativeHorizontalPositionColumn
  'box.RelativeVerticalPosition = wdRelativeVerticalPositionParagraph
  'box.LockAnchor = False

  'box.WrapFormat.AllowOverlap = True
  'box.WrapFormat.Side = wdWrapBoth
  'box.WrapFormat.DistanceTop = CentimetersToPoints(0)
  'box.WrapFormat.DistanceBottom = CentimetersToPoints(0)
  'box.WrapFormat.DistanceLeft = CentimetersToPoints(0.32)
  'box.WrapFormat.DistanceRight = CentimetersToPoints(0.32)
  'box.WrapFormat.Type = 3
  
  'box.ZOrder 5

  'box.TextFrame.MarginLeft = 7.09
  'box.TextFrame.MarginRight = 7.09
  'box.TextFrame.MarginTop = 3.69
  'box.TextFrame.MarginBottom = 3.69

  'box.IncrementLeft 47.7
  'box.IncrementTop 47.7
  'box.Left = CentimetersToPoints(1)
  'box.Top = CentimetersToPoints(0.23)
  'box.Height = 18.15
  'box.Width = 26.95

End Sub


'-------------------------------------------------------------------------------

