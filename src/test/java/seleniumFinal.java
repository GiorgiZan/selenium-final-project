import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

public class seleniumFinal {
    WebDriver driver;

    @BeforeTest
    @Parameters("browser")
    public void setup(String browser) throws Exception{
        if(browser.equalsIgnoreCase("Chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        }
        else if(browser.equalsIgnoreCase("Edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        }
        else {
            throw new Exception("Wrong browser");
        }
    }
    @Test
    public void testSwoopFinal(){
        WebDriverWait wait = new WebDriverWait(driver, 15);
        // Navigate to the swoop.ge
        driver.navigate().to("https://www.swoop.ge/");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Go to 'კინო'
        WebElement movieButton = driver.findElement(By.xpath("//li[contains(@class, 'MoreCategories')][contains(normalize-space(), 'კინო')]"));
        movieButton.click();

        //Select the first movie in the returned list and click on ‘ყიდვა’ button
        List<WebElement> allMovies = driver.findElements(By.cssSelector("div.movies-deal"));
        int yOffset = -100;
        //clicking on the first one ( თუ კავეა ისთ ფოინთი არის )
        for (int i = 0; i < allMovies.size(); i++) {
            // stale Error რომ ავიცილო
            List<WebElement> refreshedMovies = driver.findElements(By.cssSelector("div.movies-deal"));
            WebElement movie = refreshedMovies.get(i);
            String nameOfTheMovie = movie.getText();
            movie.click();

            try {
                //Scroll vertically (if necessary), and horizontally and choose ‘კავეა ისთ ფოინთი’
                WebElement linkElement = driver.findElement(By.xpath("//a[text()='კავეა ისთ ფოინთი']"));
                // თუ საჭირო გახდა სქროლი რომ ისთ ფოინთი ვიპოვოთ (Header მიშლიდა ამიტომაც scrollBy გამოყენებაც მომიწია)
                js.executeScript("arguments[0].scrollIntoView(); window.scrollBy(0, arguments[1]);", linkElement, yOffset);
                linkElement.click();
                break; // Found and clicked on the desired link, exit the loop
            } catch (NoSuchElementException e) {
                // 'კავეა ისთ ფოინთი' link not found, continue to the next movie
                System.out.println("ეს კინო: " + nameOfTheMovie + ", არ გადის კავეა ისთ ფოინთში");
                driver.navigate().back();

            }
        }


        // Check that only ‘კავეა ისთ ფოინთი’ options are returned
        List<WebElement> elements = driver.findElements(By.xpath("//div[@id='384933']//div[@class='seanse-details ui-tabs-panel ui-widget-content ui-corner-bottom']//p[@class='cinema-title' and text()='კავეა ისთ ფოინთი']"));
        for (int i = 0; i < elements.size(); i++) {
            String elementText = (String) js.executeScript("return arguments[0].textContent;", elements.get(i));
            Assert.assertEquals("კავეა ისთ ფოინთი", elementText);
        }


        // Click on last date
        List<WebElement> Dates =  driver.findElements(By.xpath("//*[@id=\"384933\"]/div/ul/li"));
        WebElement lastDate = Dates.get(Dates.size() - 1);
        lastDate.click();

        //click on last option
        WebElement lastElement = elements.get(elements.size() - 1);
        lastElement.click();


        // collecting movie name, cinema and date for future task
        String lastDateString = lastDate.getText();
        String[] onlyDate =  lastDateString.split(" ");

        //cinema არის ეს
        String lastElemntString = lastElement.getText();

        WebElement movieName = driver.findElement(By.cssSelector("p.name"));
        String movieNameString = movieName.getText();



        //getting  movie name, cinema and datetime to verify that it is valid
        WebElement actualMovieTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p.movie-title")));

        WebElement actualCinema = driver.findElements(By.cssSelector("p.movie-cinema")).get(0);

        WebElement actualMovieDate = driver.findElements(By.cssSelector("p.movie-cinema")).get(1);
        String[] actualDateOnly =  actualMovieDate.getText().split(" ");

        //Check in opened popup that movie name, cinema and datetime is valid
        Assert.assertEquals(actualDateOnly[0], onlyDate[0]);
        System.out.println("\nActual Date: " + actualDateOnly[0]);
        System.out.println("Expected Date: " + onlyDate[0]);
        Assert.assertEquals(actualCinema.getText(), lastElemntString);
        System.out.println("Actual Cinema: " + actualCinema.getText());
        System.out.println("Expected Cinema: " + lastElemntString);
        Assert.assertEquals(actualMovieTitle.getText(), movieNameString);
        System.out.println("Actual Title: " + actualMovieTitle.getText());
        System.out.println("Expected Title: " + movieNameString);


        //Choose any vacant place
        WebElement freeSeatElement = driver.findElement(By.xpath("//div[@class='seat free']"));
        freeSeatElement.click();


        // Register for a new account
        WebElement registration =wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p.register")));
        registration.click();
        //სახელი
        driver.findElement(By.cssSelector("input#pFirstName")).sendKeys("george");
        //გვარი
        driver.findElement(By.cssSelector("input#pLastName")).sendKeys("zandarashvili");
        //Fill all fields with valid data except for email
        //ელ.ფოსტა (არასწორი გვინდა ამიტომაც @ არ აქვს)
        driver.findElement(By.cssSelector("input#pEmail")).sendKeys("GiorgiTibisishi.com");
        //მობიულური
        driver.findElement(By.cssSelector("input#pPhone")).sendKeys("512123321");
        //დაბადების თარიღი
        driver.findElement(By.cssSelector("input#pDateBirth")).sendKeys("09/06/2003");
        //აირჩიე სქესი
        Select chooseGender = new Select(driver.findElement( By.cssSelector("select#pGender")));
        chooseGender.selectByVisibleText("კაცი");
        //პაროლი
        driver.findElement(By.cssSelector("input#pPassword")).sendKeys("BtuLoveTibisi");
        //გაიმეორე პაროლი
        driver.findElement(By.cssSelector("input#pConfirmPassword")).sendKeys("BtuLoveTibisi");
        //რეგისტრაციის კნოპკა
        WebElement registrationButton = driver.findElement(By.xpath("//input[@type='button' and @value='რეგისტრაცია']"));
        registrationButton.click();
        //ერრორ მესიჯი
        WebElement checkErrorMessage =  driver.findElement(By.cssSelector("p#physicalInfoMassage"));

        //check that error message ‘მეილის ფორმატი არასწორია!' is appear
        Assert.assertEquals(checkErrorMessage.getText(), "მეილის ფორმატი არასწორია!");
        System.out.println(checkErrorMessage.getText().equals("მეილის ფორმატი არასწორია!"));
    }



    @AfterTest
    public void afterClass() {
        driver.quit();
    }
}


