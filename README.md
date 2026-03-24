# RiverSealTrigger

This is a plugin module for [PAMGuard](https://www.pamguard.org/) DCL
software. It is part of a processing chain which sits between the open
source Tritech acquisition and detection plugins available on
[Github](https://github.com/douggillespie/TritechAcquisitionPlugin) and
the GenusWave control system which can fire off a seal startle device.

It is compatible with [PAMGuard](https://www.pamguard.org/) V2.2.18 and
[Tritech Acquisition
Plugin](https://github.com/douggillespie/TritechAcquisitionPlugin) 2.0
or later.

Decisions are made based on direction of river flow (relative to sonar
frame) and some user defined 'lines' across the river indicating that a
seal is swimming upstream into the area of interest.

## Installation

Ensure you have an up to date PAMGuard installation and Tritech Acquisition Plugin 
installed.

Download the latest release and copy the file TritechRiverTriggerV2_00.jar into the
plugins folder of your PAMGuard installation (most likely C:\\Program Files\\Pamguard\\plugins).

## V2.0, 24 March 2026

Updated to use absolute positions of detection providing track
information. This is in support of two sonar systems, with one sonar
generally located on each river bank, so pointing in different
directions. Whereas previous versions only used relative coordinates
within the sonar frame, the software now uses absolute coordinates,
based on sonar position information.

## V1.2, 16 October 2024

Added functionality to divide river into mid stream section and bank
sections. Trigger parameters can be set independently for the bank and
mid steam regions to allow for stricter criteria mid stream, but still
have a very sensitive detector on the far bank.

## People

Code written by Doug Gillespie (dg50). Project PI is Rob Harris (rh18).
Any other enquiries to SMRU director Carol Sparling (ces6).
