# Лаборатори №8 — Mockito mock объектууд

**Оюутны нэр:** Б. Энхбаяр  
**Оюутны код:** B232270018

## Орчны мэдээлэл

`java -version`:

```text
openjdk version "21.0.5" 2024-10-15 LTS
OpenJDK Runtime Environment Temurin-21.0.5+11 (build 21.0.5+11-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.5+11 (build 21.0.5+11-LTS, mixed mode, sharing)
```

`mvn -version`:

```text
Apache Maven 3.9.15
Java version: 21.0.5, vendor: Eclipse Adoptium, runtime: C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot
OS name: "windows 11", version: "amd64"
```

## Хэрэгжүүлэлт

`CourseRegistrationService`-ийн бизнес логикийг repository болон email илгээгчийн Mockito mock-оор тусгаарлан тестэлсэн. A8 параметржүүлсэн тестийн мөр бүрд өөр stub ашигладаг тул Mockito strict stubs горим ашиглагдаагүй stub-ийг алдаа гэж үзэхээс сэргийлж `lenient()` хэрэглэсэн.

Ногоон тестийн тоо: **10** (`results/mvn-test.txt`). `java -version` ба `mvn -version`-ийг хоёуланг нь Temurin JDK 21.0.5-аар ажиллуулсан. Mockito-ийн Byte Buddy agent энэ Windows орчинд JVM-д өөрөө холбогдоход саатаж байсан тул тестийг `mvn "-DargLine=-javaagent:<byte-buddy-agent.jar>" test` гэж ажиллуулсан; энэ нь тайланд бүрэн Maven гаралт болон `BUILD SUCCESS` өгсөн.

## Мутацийн туршилтын үр дүн

| Мутант | Унасан тест | Log |
|---|---|---|
| 1 — идэвхгүй оюутны шалгалтыг устгасан | A3 `идэвхгүйОюутан_бүртгүүлэхэдАлдааБуцааж_цаашШалгахгүй` ба A8-ийн `inactive` мөр хоёулаа `ERROR_INACTIVE_STUDENT` хүлээсэн ч `ERROR_NO_COURSE` авсан | `results/mvn-test-mutant-1.txt` — 10 тест, 2 failure, `BUILD FAILURE` |
| 2 — хадгалалтын дуудлагыг устгасан | A1 `идэвхтэйОюутан_бүртгүүлэхэдOkБуцааж_хадгалжИмэйлИлгээнэ` ба A5 `имэйлИлгээгчАлдааШидэхэд_бүртгэлOkБайж_хадгалагдана` дээр `saveRegistration` дуудагдаагүй | `results/mvn-test-mutant-2.txt` — 10 тест, 2 failure, `BUILD FAILURE` |
| 3 — courseId null шалгалтыг устгасан | A6 `nullОролт_альНэгАргументNullБол_IllegalArgumentException` дээр courseId null байхад exception гараагүй | `results/mvn-test-mutant-3.txt` — 10 тест, 1 failure, `BUILD FAILURE` |

Бүх гурван мутант дор хаяж нэг тестээр баригдсан. Гурав дахь мутантыг илрүүлэхийн тулд A6 тестэд `studentId` болон `courseId`-ийн null тохиолдлыг тус тусад нь шалгасан.

## Дүгнэлт

Mock нь тестийн үед Mockito-гийн тохируулсан хариу болон дуудлагын бүртгэлийг үүсгэдэг объект юм. Fake нь интерфейсийг өөрөө хэрэгжүүлсэн, бодитой ажилладаг хялбар орлуулалт байдаг. Энэ тестэд өгөгдлийн сан, SMTP зэрэг гаднын хамаарлыг mock хийсэн. Харин тестэлж буй бизнес логик, энгийн өгөгдлийн объект, өөрийн шалгаж буй бодит кодыг mock хийх нь тестийн утгыг бууруулна. `verify`-г шаардлагагүй нарийвчилбал хэрэгжүүлэлтийн дотоод дараалалд хэт баригдаж, зөв өөрчлөлт хийхэд тестүүд хэврэг болно. Тиймээс энд зөвхөн хадгалалт болон имэйл зэрэг ажиглах ёстой гаднын нөлөөг шалгасан. `verifyNoInteractions` нь оюутны идэвхгүй үед хамаарлууд дуудагдахгүйг батална.

## Төслийн бүтэц

```text
pom.xml
src/main/java/mn/edu/must/sqat/
src/test/java/mn/edu/must/sqat/CourseRegistrationServiceTest.java
results/
```


