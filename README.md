# Backend Processor

## Role:
For a set of functional dependencies (FDs)...
* Generate all appropriate candidate keys (CKs)
* Generate the canonical cover 
* Decompose a relation into 2NF, 3NF & BCNF

### TODO:
- [ ] Create CK generation script
    - [ ] Add sufficient # of test cases
    - [ ] Deploy
- [ ] Create canonical cover generation script
    - [ ] Add sufficient # of test cases
    - [ ] Deploy
- [ ] Decompose a relation into 2NF
    - [ ] Add sufficient # of test cases
    - [ ] Deploy    
- [ ] Decompose a relation into 3NF
    - [ ] Add sufficient # of test cases
    - [ ] Deploy
- [ ] Decompose a relation into BCNF
    - [ ] Add sufficient # of test cases
    - [ ] Deploy  
    
#### Candidate Key Generation
1. Determining mandatory attributes intuition: <br>    
      R(&alpha;<sub>1</sub>, &alpha;<sub>2</sub>, ..., &alpha;<sub>n</sub>) <br>
      Where R is our relation. <br><br>
      &beta;<sub>1</sub> ⇒ &mu;<sub>1</sub><br>
      &beta;<sub>2</sub> ⇒ &mu;<sub>2</sub><br>
      &beta;<sub>3</sub> ⇒ &mu;<sub>3</sub><br>
      .<br>.<br>
      &beta;<sub>n</sub> ⇒ &mu;<sub>n</sub><br><br>
      Where &beta;<sub>i</sub>, &mu;<sub>i</sub> ∈ &sum;<super>*</super> {&alpha;<sub>1</sub>, &alpha;<sub>2</sub>, ..., &alpha;<sub>n</sub>}
      <br>for i &le; n
      <br><br>
      &Alpha; = [&nbsp;R-&nbsp;[(&mu;<sub>1</sub> &cup; &mu;<sub>2</sub> &cup; &mu;<sub>3</sub> &cup; ... &cup; &mu;<sub>n</sub>)&nbsp;&cap; R]&nbsp;]
      <br>
      Where &Alpha; represents the attributes of the relation that are <b>NOT</b> included within the 
      derivation portion (right side), of the FD 
      <br><br>
      &Beta; = [&nbsp;R-&nbsp;[(&beta;<sub>1</sub>&mu;<sub>1</sub> &cup; &beta;<sub>2</sub>&mu;<sub>2</sub> &cup; &beta;<sub>3</sub>&mu;<sub>3</sub> &cup; ... &cup; 
      &beta;<sub>n</sub>&mu;<sub>n</sub>)&nbsp;&cap; R]&nbsp;]
      <br>
      Where &Beta; represents the attributes of the relation that are <b>NOT</b> included in 
      any of the FDs, on either side 
      <br><br>
      <br>
      &gamma; = &Alpha;&cup;&Beta; 
      <br>
      Where &gamma; is the set of attributes that <b>must be included within the candidate key</b> 
      <br><br>

2. Adding <i>accessory</i> attributes onto the necessary minimal attributes (intuition above):
    * In this context, an <b>accessory attribute</b> is one that is not part of our set of necessary minimal attributes, but exists within
    the relational schema<br>
    <br> A heuristic approach is taken, where accessory attributes are added onto a working CK attribute, and a heuristic based on the amount of unmatched FDs is taken.
    The accessory attribute that is associated with the smallest heuristic value, is added onto the working CK attribute, and the
    process repeats until we've reached a minimal heuristic value of 0. 