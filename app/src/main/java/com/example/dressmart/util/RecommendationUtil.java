package com.example.dressmart.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import androidx.palette.graphics.Palette;

import com.example.dressmart.models.RecommendedOutfit;
import com.example.dressmart.models.WeatherCondition;
import com.example.dressmart.models.parse.Garment;
import com.parse.ParseException;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class RecommendationUtil {

    public static RecommendedOutfit getRecommendation(WeatherCondition weatherCondition, HashMap<String, List<Garment>> closet) {
        Comparator<Garment> dateComparator = new Comparator<Garment>() {
            @Override
            public int compare(Garment g1, Garment g2) {
                return g1.getDateLastWorn().compareTo(g2.getDateLastWorn());
            }
        };
        // sort each list by ascending date last worn
        Collections.sort(closet.get("Top"), dateComparator);
        Collections.sort(closet.get("Bottoms"), dateComparator);
        Collections.sort(closet.get("Outer"), dateComparator);
        Collections.sort(closet.get("Shoes"), dateComparator);

        Garment top = closet.get("Top").get(0);
        Garment bottoms = closet.get("Bottoms").get(0);
        Garment outer = null;
        Garment shoes = closet.get("Shoes").get(0);

        // each list is sorted from least recently worn to most recently worn,
        // so the items that will be recommended won't be the most recent items
        for (Garment item : closet.get("Top")) {
            if (weatherCondition.getAvgTemp() < 60) {
                if (item.getSubtype().equals("Long-Sleeved")) {
                    top = item;
                    break;
                }
            } else {
                if (item.getSubtype().equals("Short-Sleeved")) {
                    top = item;
                    break;
                }
            }
        }
        for (Garment item : closet.get("Bottoms")) {
            if (weatherCondition.getAvgTemp() < 60) {
                if (item.getSubtype().equals("Pants")) {
                    bottoms = item;
                    break;
                }
            } else if (weatherCondition.getAvgTemp() >= 60) {
                if (item.getSubtype().equals("Shorts")) {
                    bottoms = item;
                    break;
                }
            }
        }
        for (Garment item : closet.get("Outer")) {
            if (weatherCondition.getAvgTemp() < 40) {
                if (item.getSubtype().equals("Coat")) {
                    outer = item;
                    break;
                }
            } else if (weatherCondition.getAvgTemp() < 60) {
                if (weatherCondition.getWindSpeed() > 15 || weatherCondition.getChanceOfPrecip() > 50) {
                    if (item.getSubtype().equals("Jacket")) {
                        outer = item;
                        break;
                    }
                } else {
                    if (item.getSubtype().equals("Sweater")) {
                        outer = item;
                        break;
                    }
                }
            }
        }
        for (Garment item : closet.get("Shoes")) {
            if (weatherCondition.getAvgTemp() < 40 || weatherCondition.getChanceOfPrecip() > 70) {
                if (item.getSubtype().equals("Boots")) {
                    shoes = item;
                    break;
                }
            } else if (weatherCondition.getAvgTemp() > 70 && !weatherCondition.getConditions().equals("Overcast")) {
                if (item.getSubtype().equals("Sandals")) {
                    shoes = item;
                    break;
                }
            } else {
                if (item.getSubtype().equals("Sneakers")) {
                    shoes = item;
                    break;
                }
            }
        }
        return new RecommendedOutfit(top, bottoms, outer, shoes);
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
        if (saturation <= 10) {
            return "gray";
        } else if (hue > 340 || hue <= 10) {
            return "red";
        } else if (hue > 10 && hue <= 40) {
            return "orange";
        } else if (hue > 40 && hue <= 70) {
            return "yellow";
        } else if (hue > 70 && hue <= 160) {
            return "green";
        } else if (hue > 160 && hue <= 260) {
            return "blue";
        } else if (hue > 260 && hue <= 290) {
            return "purple";
        } else if (hue > 290 && hue <= 340){
            return "pink";
        } else if (lightness <= 0.1) {
            return "black";
        } else if (lightness >= 0.95) {
            return "white";
        } else {
            return "";
        }
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
        return (color1.equals("yellow") && color2.equals("purple")) || (color1.equals("purple") && color2.equals("yellow"))
                || (color1.equals("orange") && color2.equals("blue")) || (color1.equals("blue") && color2.equals("orange"))
                || (color1.equals("green") && color2.equals("red")) || (color1.equals("red") && color2.equals("green"))
                || (color1.equals("black") && color2.equals("white")) || (color1.equals("white") && color2.equals("black"));
    }


}
