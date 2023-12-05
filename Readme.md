
[example-paper-and-bibliography](https://joss.readthedocs.io/en/latest/submitting.html#example-paper-and-bibliography) 

```bash
docker run --rm \
    --volume $PWD:/data \
    --user $(id -u):$(id -g) \
    --env JOURNAL=joss \
    openjournals/inara
```
    