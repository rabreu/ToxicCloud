package br.toxiccloud;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.LayeredWordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundaryBackground;
import com.kennycason.kumo.font.FontWeight;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ToxicCloud {

    final int WIDGTH = 1000;
    final int HEIGHT = 800;
    final String STOPWORDS_FILE = "src/main/resources/stopwords.txt";
    final String IMAGE_OUTPUT = "toxiccloud_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";

    public void start (String inputFile) {
        try {
            List<String> phrases;
            if(StringUtil.isBlank(inputFile)) {
                final String outputFile = "output/txt/arquivo_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".txt";

                Document doc = Jsoup.connect("https://docs.google.com/spreadsheets/d/1u1_8ND_BY1DaGaQdu0ZRZPebrOaTJekE9hyw_7BAlzw/htmlview?usp=drivesdk")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                        .header("Accept-Language", "*")
                        .timeout(100000)
                        .get();

                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

                doc.body().getElementsByTag("tbody")
                        .forEach(e -> e.getElementsByTag("tr")
                                .forEach(tr -> tr.getElementsByClass("s3")
                                        .forEach(td -> {
                                            try {
                                                if(StringUtils.isNotBlank(td.text())) {
                                                    writer.write(td.text());
                                                    writer.newLine();
                                                }
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                        })));

                phrases = Files.readAllLines(Paths.get(outputFile));
            } else {
                phrases = Files.readAllLines(Paths.get(inputFile));
            }

            FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
            frequencyAnalyzer.setWordFrequenciesToReturn(300);
            frequencyAnalyzer.setMinWordLength(5);
            frequencyAnalyzer.setStopWords(Files.readAllLines(Paths.get(STOPWORDS_FILE)));

            List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(phrases);
            LayeredWordCloud  toxicCloud = new LayeredWordCloud(2, new Dimension(WIDGTH, HEIGHT), CollisionMode.PIXEL_PERFECT);
            toxicCloud.setPadding(0, 1);
            toxicCloud.setPadding(1,1);
            toxicCloud.setBackgroundColor(new Color(0xFF2F303B, true));
            toxicCloud.setBackground(0, new PixelBoundaryBackground(Files.newInputStream(Paths.get("src/main/resources/linkedin_fg.png"))));
            toxicCloud.setBackground(1, new PixelBoundaryBackground(Files.newInputStream(Paths.get("src/main/resources/linkedin_bg.png"))));
            toxicCloud.setColorPalette(0, new ColorPalette(new Color(0xFFE6E6E6, true), new Color(0xD8E6E6E6, true), new Color(0xB3E6E6E6, true)));
            toxicCloud.setColorPalette(1, new ColorPalette(new Color(0x0e76a8), new Color(0xBF0E76A8, true), new Color(0x800E76A8, true)));
            toxicCloud.setFontScalar(0, new SqrtFontScalar(2, 100));
            toxicCloud.setFontScalar(1, new SqrtFontScalar(2, 100));
            toxicCloud.setKumoFont(0, new KumoFont("Comic Sans MS", FontWeight.PLAIN));
            toxicCloud.setKumoFont(1, new KumoFont("Comic Sans MS", FontWeight.PLAIN));
            toxicCloud.build(0, wordFrequencies);
            toxicCloud.build(1, wordFrequencies);

            toxicCloud.writeToFile("./output/png/" + IMAGE_OUTPUT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
