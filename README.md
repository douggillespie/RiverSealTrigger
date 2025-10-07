# RiverSealTrigger


This is a plugin module for [PAMGuard](https://www.pamguard.org/) DCL software. It is part of a processing chain which sits between 
the open source Tritech acquisition and detection plugins available on [Github](https://github.com/douggillespie/TritechAcquisitionPlugin) 
and the [GenusWave control system](https://gitlab.st-andrews.ac.uk/biology/smru/dg50/genuscontrol) which can fire off a seal startle device. 

It is compatible with PAMGuard V2.2.13 and will not work with earlier versions.

Decisions are made based on direction of river flow (relative to sonar frame) and some user defined 'lines' across the river indicating that 
a seal is swimming upstream into the area of interest. 

## V1.2, 16 October 2024
Added functionality to divide river into mid stream section and bank sections. Trigger 
parameters can be set independently for the bank and mid steam regions to allow for 
stricter criteria mid stream, but still have a very sensitive detector on the far 
bank. 

## Peope

Code written by Doug Gillespie (dg50). Project PI is Rob Harris (rh18). Any other enquiries to SMRU director Carol Sparling (ces6).