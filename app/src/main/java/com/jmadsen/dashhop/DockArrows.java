package com.jmadsen.dashhop;
final class DockArrows {
 static String glyph(String edge,boolean expanded){if("top".equals(edge))return expanded?"▴":"▾";if("left".equals(edge))return expanded?"‹":"›";if("right".equals(edge))return expanded?"›":"‹";return expanded?"▾":"▴";}
}
