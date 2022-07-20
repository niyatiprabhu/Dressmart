package com.example.dressmart.util;

import static com.example.dressmart.Constants.BOTTOMS;
import static com.example.dressmart.Constants.OUTER;
import static com.example.dressmart.Constants.SHOES;
import static com.example.dressmart.Constants.TOP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import androidx.palette.graphics.Palette;

import com.example.dressmart.R;
import com.example.dressmart.models.RecommendedOutfit;
import com.example.dressmart.models.WeatherCondition;
import com.example.dressmart.models.parse.Garment;
import com.parse.ParseException;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class RecommendationUtil {

    // constants for Garment subtypes
    private static final String LONG_SLEEVED = "Long-Sleeved";
    private static final String SHORT_SLEEVED = "Short-Sleeved";
    private static final String PANTS = "Pants";
    private static final String SHORTS = "Shorts";
    private static final String COAT = "Coat";
    private static final String JACKET = "Jacket";
    private static final String SWEATER = "Sweater";
    private static final String BOOTS = "Boots";
    private static final String SANDALS = "Sandals";
    private static final String SNEAKERS = "Sneakers";

    // constants for colors
    private static final String GRAY = "gray";
    private static final String RED = "red";
    private static final String ORANGE = "orange";
    private static final String YELLOW = "yellow";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";
    private static final String PURPLE = "purple";
    private static final String PINK = "pink";
    private static final String BLACK = "black";
    private static final String WHITE = "white";



    public static RecommendedOutfit getRecommendation(WeatherCondition weatherCondition, HashMap<String, List<Garment>> closet) {
        Comparator<Garment> dateComparator = new Comparator<Garment>() {
            @Override
            public int compare(Garment g1, Garment g2) {
                return g1.getDateLastWorn().compareTo(g2.getDateLastWorn());
            }
        };

        // sort each list by ascending date last worn
        Collections.sort(closet.get(TOP), dateComparator);
        Collections.sort(closet.get(BOTTOMS), dateComparator);
        Collections.sort(closet.get(OUTER), dateComparator);
        Collections.sort(closet.get(SHOES), dateComparator);

        // each list is sorted from least recently worn to most recently worn,
        // so the items that will be recommended won't be the most recent items
        Garment top = selectTop(weatherCondition, closet);
        Garment bottoms = selectBottoms(weatherCondition, closet);
        Garment outer = selectOuter(weatherCondition, closet);
        Garment shoes = selectShoes(weatherCondition, closet);
        return new RecommendedOutfit(top, bottoms, outer, shoes);
    }

    private static Garment selectShoes(WeatherCondition weatherCondition, HashMap<String, List<Garment>> closet) {
        Garment shoes = closet.get(SHOES).get(0);
        for (Garment item : closet.get(SHOES)) {
            if (weatherCondition.getAvgTemp() < 40 || weatherCondition.getChanceOfPrecip() > 70) {
                if (item.getSubtype().equals(BOOTS)) {
                    shoes = item;
                    break;
                }
            } else if (weatherCondition.getAvgTemp() > 70 && !weatherCondition.getConditions().equals("Overcast")) {
                if (item.getSubtype().equals(SANDALS)) {
                    shoes = item;
                    break;
                }
            } else {
                if (item.getSubtype().equals(SNEAKERS)) {
                    shoes = item;
                    break;
                }
            }
        }
        return shoes;
    }

    private static Garment selectOuter(WeatherCondition weatherCondition, HashMap<String, List<Garment>> closet) {
        Garment outer = null;
        for (Garment item : closet.get(OUTER)) {
            if (weatherCondition.getAvgTemp() < 40) {
                if (item.getSubtype().equals(COAT)) {
                    outer = item;
                    break;
                }
            } else if (weatherCondition.getAvgTemp() < 60) {
                if (weatherCondition.getWindSpeed() > 15 || weatherCondition.getChanceOfPrecip() > 50) {
                    if (item.getSubtype().equals(JACKET)) {
                        outer = item;
                        break;
                    }
                } else {
                    if (item.getSubtype().equals(SWEATER)) {
                        outer = item;
                        break;
                    }
                }
            }
        }
        return outer;
    }

    private static Garment selectBottoms(WeatherCondition weatherCondition, HashMap<String, List<Garment>> closet) {
        Garment bottoms = closet.get(BOTTOMS).get(0);
        for (Garment item : closet.get(BOTTOMS)) {
            if (weatherCondition.getAvgTemp() < 60) {
                if (item.getSubtype().equals(PANTS)) {
                    bottoms = item;
                    break;
                }
            } else if (weatherCondition.getAvgTemp() >= 60) {
                if (item.getSubtype().equals(SHORTS)) {
                    bottoms = item;
                    break;
                }
            }
        }
        return bottoms;
    }

    private static Garment selectTop(WeatherCondition weatherCondition, HashMap<String, List<Garment>> closet) {
        Garment top = closet.get(TOP).get(0);
        for (Garment item : closet.get(TOP)) {
            if (weatherCondition.getAvgTemp() < 60) {
                if (item.getSubtype().equals(LONG_SLEEVED)) {
                    top = item;
                    break;
                }
            } else {
                if (item.getSubtype().equals(SHORT_SLEEVED)) {
                    top = item;
                    break;
                }
            }
        }
        return top;
    }

    public static double calculateMatchScore(Garment top, Garment bottoms, Garment shoes) throws ParseException {
        // use Palette library to determine the dominant color in each garment
        Bitmap bitmapTop = BitmapFactory.decodeFile(top.getGarmentPicture().getFile().getPath());
        bitmapTop = scaleCenterCrop(bitmapTop, getSquareCropDimension(bitmapTop), getSquareCropDimension(bitmapTop));
        Bitmap bitmapBottoms = BitmapFactory.decodeFile(bottoms.getGarmentPicture().getFile().getPath());
        bitmapBottoms = scaleCenterCrop(bitmapBottoms, getSquareCropDimension(bitmapBottoms), getSquareCropDimension(bitmapBottoms));
        Bitmap bitmapShoes = BitmapFactory.decodeFile(shoes.getGarmentPicture().getFile().getPath());
        bitmapShoes = scaleCenterCrop(bitmapShoes, getSquareCropDimension(bitmapShoes), getSquareCropDimension(bitmapShoes));

        Palette pTop = Palette.from(bitmapTop).generate();
        Palette pBottoms = Palette.from(bitmapBottoms).generate();
        Palette pShoes = Palette.from(bitmapShoes).generate();

        Palette.Swatch topSwatch = pTop.getVibrantSwatch() == null ? pTop.getMutedSwatch() : pTop.getVibrantSwatch();
        Palette.Swatch bottomsSwatch = pBottoms.getVibrantSwatch() == null ? pBottoms.getMutedSwatch() : pBottoms.getVibrantSwatch();
        Palette.Swatch shoesSwatch = pShoes.getVibrantSwatch() == null ? pShoes.getMutedSwatch() : pShoes.getVibrantSwatch();

        String topColor = getColorFromRgb(topSwatch.getHsl());
        String bottomsColor = getColorFromRgb(bottomsSwatch.getHsl());
        String shoesColor = getColorFromRgb(shoesSwatch.getHsl());


        double score = 2.5;
        // reduce score if:
        // top and bottoms are same color family
        if (topColor.equals(bottomsColor)) {
            score -= 1;
        }
        // top and bottom are both bright colors (clash)
        if (isBright(topSwatch.getHsl()) && isBright(bottomsSwatch.getHsl())) {
            score -= 1;
        }
        // raise score if:
        // top and bottom have both light and dark (contrast)
        if (isLight(topSwatch.getHsl()) && isDark(bottomsSwatch.getHsl())
                || isDark(topSwatch.getHsl()) && isLight(bottomsSwatch.getHsl()))    {
            score += 0.5;
        }
        // top and bottom are complementary colors
        if (areComplementary(topColor, bottomsColor)) {
            score += 1;
        }
        // top matches color of shoes
        if (topColor.equals(shoesColor)) {
            score += 1;
        }
        return score;
    }

    private static Bitmap scaleCenterCrop(Bitmap source, int newHeight,
                                          int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top
                + scaledHeight);//from ww w  .j a va 2s. co m

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
                source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    private static int getSquareCropDimension(Bitmap bitmap)
    {
        //use the smallest dimension of the image to crop to
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
    }


    private static String getColorFromRgb(float[] hsl) {
        float hue = hsl[0];
        float lightness = hsl[2];
        float saturation = hsl[1];
        String color = "";
        if (saturation <= 10) {
            color += GRAY;
        } else if (hue > 340 || hue <= 10) {
            color += RED;
        } else if (hue > 10 && hue <= 40) {
            color += ORANGE;
        } else if (hue > 40 && hue <= 70) {
            color += YELLOW;
        } else if (hue > 70 && hue <= 160) {
            color += GREEN;
        } else if (hue > 160 && hue <= 260) {
            color += BLUE;
        } else if (hue > 260 && hue <= 290) {
            color += PURPLE;
        } else if (hue > 290 && hue <= 340){
            color += PINK;
        } else if (lightness <= 0.1) {
            color += BLACK;
        } else if (lightness >= 0.95) {
            color += WHITE;
        }
        return color;
    }

    private static boolean isBright(float[] hsl) {
        float saturation = hsl[1];
        return saturation >= 0.7;
    }

    private static boolean isLight(float[] hsl) {
        float lightness = hsl[2];
        return lightness >= 0.75;
    }

    private static boolean isDark(float[] hsl) {
        float lightness = hsl[2];
        return lightness <= 25;
    }

    private static boolean areComplementary(String color1, String color2) {
        return (color1.equals(YELLOW) && color2.equals(PURPLE)) || (color1.equals(PURPLE) && color2.equals(YELLOW))
                || (color1.equals(ORANGE) && color2.equals(BLUE)) || (color1.equals(BLUE) && color2.equals(ORANGE))
                || (color1.equals(GREEN) && color2.equals(RED)) || (color1.equals(RED) && color2.equals(GREEN))
                || (color1.equals(BLACK) && color2.equals(WHITE)) || (color1.equals(WHITE) && color2.equals(BLACK));
    }


}
