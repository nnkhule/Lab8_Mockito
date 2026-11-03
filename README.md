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

Ногоон тестийн тоо: **10** (`results/mvn-test.txt`). `java -version` ба `mvn -version`-ийг хоёуланг нь Temurin JDK 21.0.5-аар ажиллуулсан. Mockito-ийн Byte Buddy agent өөрөө JVM-д холбогдох үед JDK 21 дээр динамик agent ачаалуулах хязгаарлалттай тул `pom.xml`-ийн Surefire `argLine` agent-ийг JVM эхлэхэд шууд ачаалуулахаар тохируулсан. JDK 21 ашигласан ч `maven.compiler.release` нь 17 хэвээр тул код Java 17-той нийцтэй compile хийгдэнэ. Шинэ цэвэр checkout дээр нэмэлт параметргүй `mvn test` ажиллаж, 10/10 тест ба `BUILD SUCCESS` өгсөн.

## Мутацийн туршилтын үр дүн

| Мутант | Унасан тест | Log |
|---|---|---|
| 1 — идэвхгүй оюутны шалгалтыг устгасан | A3 болон A8-ийн `inactive` мөр хоёулаа `ERROR_INACTIVE_STUDENT` хүлээсэн ч `ERROR_NO_COURSE` авсан. A8 мөр course `CS201`-ийг шалгахад false default авдаг тул яг хүлээсэн `ERROR_INACTIVE_STUDENT`, ирсэн `ERROR_NO_COURSE` гэж failure гарсан. | `results/mvn-test-mutant-1.txt` — 10 тест, 2 failure, `BUILD FAILURE` |
| 2 — хадгалалтын дуудлагыг устгасан | A1 `идэвхтэйОюутан_бүртгүүлэхэдOkБуцааж_хадгалжИмэйлИлгээнэ` ба A5 `имэйлИлгээгчАлдааШидэхэд_бүртгэлOkБайж_хадгалагдана` дээр `saveRegistration` дуудагдаагүй | `results/mvn-test-mutant-2.txt` — 10 тест, 2 failure, `BUILD FAILURE` |
| 3 — courseId null шалгалтыг устгасан | A6 `nullОролт_альНэгАргументNullБол_IllegalArgumentException` дээр courseId null байхад exception гараагүй | `results/mvn-test-mutant-3.txt` — 10 тест, 1 failure, `BUILD FAILURE` |

Бүх гурван мутант дор хаяж нэг тестээр баригдсан. Гурав дахь мутантыг илрүүлэхийн тулд A6 тестэд `studentId` болон `courseId`-ийн null тохиолдлыг тус тусад нь шалгасан.

## Дүгнэлт

Mock нь Mockito-гоор тохируулсан хариу болон дуудлагын бүртгэлтэй тестийн объект юм. Fake гэдэг нь интерфейсийг өөрөө хэрэгжүүлсэн энгийн бодит орлуулалт бөгөөд жишээ нь `HashMap<String, Student>` хадгалдаг `MapStudentRepository` байж болно. Fake нь санах ойн төлөв өөрчлөгдөх олон үйлдлийн урсгалыг шалгахад тохиромжтой. Харин ховор алдаа, email шиг гаднын үйлчилгээг дуурайлгах эсвэл тодорхой дуудлага хийгдсэнийг батлахад mock-ийг fake-ээс илүүд үзнэ. Тестэлж буй бизнес логик, энгийн өгөгдлийн объект, бодит кодыг өөрийг нь mock хийх нь тестийн үнэ цэнийг бууруулна. `verify`-г хэт нарийвчлахад тест хэрэгжүүлэлтийн дотоод дараалалд баригдаж хэврэг болно. Тиймээс энд зөвхөн хадгалалт, имэйл зэрэг ажиглах шаардлагатай гаднын үйлдлийг баталсан. `verifyNoInteractions` нь идэвхгүй оюутанд email огт хүрэхгүйг шалгана.

## Төслийн бүтэц

```text
pom.xml
src/main/java/mn/edu/must/sqat/
src/test/java/mn/edu/must/sqat/CourseRegistrationServiceTest.java
results/
```



