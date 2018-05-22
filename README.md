# Imglib2 resizing regression demo

This repository demonstrates a regression introduced in imglib2-4.6.0

## Branches
- The code on the `master` branch (based on `pom-scijava-17.1.1`) shows the old behavior
- The code on the `pom-scijava-17.2.0-regression` branch shows, that updating
	the parent pom to `pom-scijava-17.2.0` creates a regression, the Resizer plugin
	now produces a different result (the version of imglib2 in this setup is `4.6.0`.
- The code on the `pom-scijava-17.2.0-imglib2-4.5.0` branch shows, that setting
	the imglib2 version to `4.5.0` restores the old behavior, proving that the
	regression has been introduced in `imglib-4.6.0`

## How to run the test

1. Import the project into your IDE
2. Switch to the desired branch
3. Run the main method in the `Resize.java` class.
4. View the console output to see the different results


## visual diff:

#### Input Image slice: 
![img-original.png](https://i.imgur.com/hMUFNCC.png)


#### Result old slice:
![img-old.png](https://i.imgur.com/DaGexef.png)


#### Result new slice:
![img-new.png](https://i.imgur.com/UaiGiDq.png)

#### Diff img slice:
![img-diff.png](https://i.imgur.com/g87UCZ0.png)